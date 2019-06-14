package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.*;
import cn.com.cybertech.model.*;
import cn.com.cybertech.service.AppSecurityService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.HttpClientUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import cn.com.cybertech.tools.filter.FilterParamUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

@Service("appSecurityService")
public class AppSecurityServiceImpl implements AppSecurityService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AppConfigMapper appConfigMapper;

    @Autowired
    private CommonConfigMapper commonConfigMapper;

    @Autowired
    private CommonDictsMapper commonDictsMapper;

    @Autowired
    private JedisPool jedisPool;

    @Override
    public RestResponse queryToken(RestResponse response, String appId, String secret) {

        // 验证三方应用的凭证及密钥
        AppInfo appInfo = appInfoMapper.queryAppByAppId(appId);
        if (appInfo == null) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_SELECT);
        }

        // 获取票据并缓存票据
        String token = cacheTokenStr(appInfo);
        if (StringUtils.isBlank(token)) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_TOKEN);
        }

        // 组装全局票据对象
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put(CodeUtil.TOKEN_FILED, token);
        resultMap.put(CodeUtil.TOKEN_FILED_EXPIRES, CodeUtil.TOKEN_EXPIRE);
        response.put("datas", resultMap);
        return response;
    }

    @Override
    public RestResponse userlogin(RestResponse response, String appId, String userId, String password, String platform) {
        AppInfo appInfo = appInfoMapper.queryAppByAppId(appId); //验证应用
        if (appInfo == null) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_SELECT);
        }

        //查询用户
        String newId = userId + "@" + appInfo.getAppId();
        AppUser user = appUserMapper.getAppUserById(newId);
        if (user == null) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT);
        }

        // 验证密码
//        String newPwd = EncryptUtils.MD5Encode(phone + password + "*!!");
        if (!user.getUserPasswd().equals(password)) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_PASSWORD);
        }

        // 获取用户及企业的相关信息
        Map<String, Object> infos = Maps.newHashMap();
        Map<String, Object> userMap = Maps.newHashMap();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUserName());
        infos.put("user", userMap);

        List<AppConfig> appConfigs = appConfigMapper.getAppConfigListByAppId(appInfo.getId());
        Object appConfigsFilter = FilterParamUtil.filterParam(appConfigs, FilterParamUtil.APPCONFIG_FILTER);
        infos.put("settingList", appConfigsFilter);

        List<CommonConfig> commonList = findCommonConfig(platform, null);
        Object commonFilter = FilterParamUtil.filterParam(commonList, FilterParamUtil.COMMON_FILTER);
        infos.put("commonList", commonFilter);
        addToken(infos, user, platform);
        response.retDatas(infos);
        return response;
    }

    public List<CommonConfig> findCommonConfig(String platform, String version) {
        if (StringUtils.isBlank(version)) {//不传版本号查询当前平台最新版本
            version = commonConfigMapper.queryMaxVesrion(platform);
        }

        // 检查平台是否存在
        CommonDicts dict = commonDictsMapper.findPlatform(1, platform);
        if (dict == null) {
            throw new ValueRuntimeException(MessageCode.PLATFORM_NEXIST_ERR);
        }

        //4位变3位
        version = pmVersion2ConfigVersion(version);
        List<CommonConfig> commonConfigs = commonConfigMapper.findCommonConfig(platform, version);
        return commonConfigs;
    }

    private String pmVersion2ConfigVersion(String pmVersion) {
        // 检查版本公共配置是否已经存在
        String versionStr = pmVersion;
        String[] versionStrArr = versionStr.split("\\.");
        if (versionStrArr.length != 3 && versionStrArr.length != 4) {
            throw new ValueRuntimeException(MessageCode.PLATFORM_ERR_VERSION);
        }
        if (versionStrArr.length == 4) {
            //新版是4位
            versionStr = versionStr.substring(0, versionStr.lastIndexOf("."));
        }
        return versionStr;
    }

    private void addToken(Map<String, Object> infos, AppUser user, String platform) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        try {
            String token = HttpClientUtil.getUUID();
            String redis_key = CodeUtil.REDIS_APPLOGIN_PREFIX + user.getId();
            int expiredTime = 7 * 24 * 3600;
            jedis.hset(redis_key, platform, token + ":" + System.currentTimeMillis());

            infos.put("expire_time", expiredTime);
            infos.put("token", token + "@" + platform);
        } catch (Exception e) {
            throw new ValueRuntimeException(MessageCode.PLATFORM_ERR_TOKEN);
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取票据并缓存票据
     */
    private String cacheTokenStr(AppInfo appInfo) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.select(CodeUtil.REDIS_DBINDEX);

            // 获取旧的票据, 如果未超时，直接返回
            String token = jedis.get(appInfo.getAppId());
            if (StringUtils.isNotBlank(token)) {
                String expire = jedis.hget(token, CodeUtil.TOKEN_FILED_EXPIRES);
                //未超时
                if (StringUtils.isNotBlank(expire) && Long.valueOf(expire) < System.currentTimeMillis()) {
                    return token;
                }
                jedis.del(token);
            }

            // 生成新的票据
            token = HttpClientUtil.getUUID().toUpperCase();
            // 缓存票据对应的应用信息
            Map<String, String> hash = Maps.newHashMap();
            hash.put(CodeUtil.TOKEN_FILED_APPID, appInfo.getAppId());
            hash.put(CodeUtil.TOKEN_FILED_SECRET, appInfo.getAppSecret());
            hash.put(CodeUtil.TOKEN_FILED_NAME, appInfo.getName());
            hash.put(CodeUtil.TOKEN_FILED_ID, String.valueOf(appInfo.getId()));
            hash.put(CodeUtil.TOKEN_FILED_EXPIRES, System.currentTimeMillis() + "");

            jedis.set(appInfo.getAppId(), token);
            jedis.hmset(token, hash);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }
}