package cn.com.cybertech.config.redis;

import cn.com.cybertech.model.SysUser;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
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
        } finally {
            jedis.close();
        }
        return stringMap;
    }

    public Long del(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        Long del = 0l;
        try {
            del = jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return del;
    }

    public WebUser getUser(String key) {
        WebUser webUser = new WebUser();
        Map<String, String> map = hgetAll(key);
        if (map.isEmpty()) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT);
        }
        webUser.setId(Long.valueOf(map.get("userId")));
        webUser.setUserName(map.get("userName"));
        webUser.setNickName(map.get("nickName"));
        webUser.setCompanyId(getIntValue(map, "companyId"));
        webUser.setRoleId(getIntValue(map, "roleId"));
        webUser.setSource(map.get("source"));
        return webUser;
    }


    public SysUser getSysUser(String key) {
        SysUser sysUser = new SysUser();
        Map<String, String> map = hgetAll(key);
        if (map.isEmpty()) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT);
        }
        sysUser.setId(Long.valueOf(map.get("userId")));
        sysUser.setUserName(map.get("userName"));
        sysUser.setNickName(map.get("nickName"));
        sysUser.setRoleId(getIntValue(map, "roleId"));
        sysUser.setSource(map.get("source"));
        return sysUser;
    }

    private Integer getIntValue(Map<String, String> map, String key) {
        if (StringUtils.isNotBlank(map.get(key))) {
            return Integer.valueOf(map.get(key));
        }
        return null;
    }
}
