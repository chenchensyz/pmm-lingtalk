package cn.com.cybertech.config.redis;

import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@Component
public class RedisTool {

    @Autowired
    private JedisPool jedisPool;

    public Map<String, String> hgetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        Map<String, String> stringMap = Maps.newHashMap();
        try {
            stringMap = jedis.hgetAll(key);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return stringMap;
    }

    public WebUser getUser(String key) {
        WebUser webUser=new WebUser();
        Map<String, String> map = hgetAll(key);
        if(map.isEmpty()){
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT);
        }
        webUser.setId(Long.valueOf(map.get("userId")));
        webUser.setUserName(map.get("userName"));
        webUser.setNickName(map.get("nickName"));
        webUser.setCompanyId(Integer.valueOf(map.get("companyId")));
        webUser.setRoleId(Integer.valueOf(map.get("roleId")));
        webUser.setSource(map.get("source"));
        return webUser;
    }
}
