package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppInfoMapper;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class BaseServiceImpl {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private AppInfoMapper appInfoMapper;

    /**
     * 验证票据
     *
     * @param token 票据
     * @return
     */
    protected String valiToken(String token) {
        Jedis jedis = jedisPool.getResource();
        String ret = null;
        try {
            jedis.select(CodeUtil.REDIS_DBINDEX);
            ret = jedis.hget(token, CodeUtil.TOKEN_FILED_APPID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return ret;
    }

    protected AppInfo getAppByToken(String token) {
        // 1. 查询票据是否有效
        String appId = valiToken(token);
        if (StringUtils.isBlank(appId)) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_SECURITY);
        }

        AppInfo appInfo = appInfoMapper.queryAppByAppId(appId);
        if (appInfo == null) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_SELECT);
        }
        return appInfo;
    }

    //通过appId和userId组成sdk的im用户id
    public String createAppUser(String userId, String appId) {
        String newId = userId + "@" + appId;
        return newId;
    }
}
