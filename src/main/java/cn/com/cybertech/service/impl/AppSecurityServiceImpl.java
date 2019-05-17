package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.*;
import cn.com.cybertech.model.*;
import cn.com.cybertech.service.AppSecurityService;
import cn.com.cybertech.service.AppUserService;
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

import java.util.*;

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
            jedis.hset(redis_key, platform, token + ":" + expiredTime);

            infos.put("expire_time", expiredTime);
            infos.put("token", token);
        } catch (Exception e) {
            throw new ValueRuntimeException(MessageCode.PLATFORM_ERR_TOKEN);
        } finally {
            jedis.close();
        }
    }
}