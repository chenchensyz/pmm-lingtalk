package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.WebUserMapper;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebUserService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.EncryptUtils;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

@Service("webUserService")
public class WebUserServiceImpl implements WebUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUserServiceImpl.class);

    @Autowired
    private WebUserMapper webUserMapper;

    @Autowired
    private JedisPool jedisPool;

    @Override
    public List<WebUser> getWebUserList(WebUser webUser) {
        return webUserMapper.getList(webUser);
    }

    @Override
    public WebUser getLoginInfoByPhone(String phone, String companyId) {
        return null;
    }

    @Override
    public Map<String,Object> login(WebUser webUser, String platform) {
        WebUser user = webUserMapper.getLoginInfoByPhone(webUser.getPhone(), webUser.getCompanyId());
        Map<String,Object> resultMap=Maps.newHashMap();
        if (user == null) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT); //用户不存在
        }

        String newPwd = EncryptUtils.MD5Encode(webUser.getPhone() + webUser.getPassword() + "*!!");
        if (!newPwd.equals(user.getPassword())) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_PASSWORD); //用户不存在
        }

        if (user.getState() == 0) {
            throw new ValueRuntimeException(MessageCode.USERINFO_DISABLE); //用户已被禁用
        }
        //生成token
        String token = EncryptUtils.MD5Encode(platform + webUser.getPhone() + webUser.getCompanyId());
        LOGGER.info(token);

        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        try {
            Map<String, String> map = Maps.newHashMap();
            map.put("phone", user.getPhone());
            map.put("userName", user.getUserName());
            map.put("nickName", user.getNickName());
            map.put("companyId", user.getCompanyId() + "");
            map.put("timestamp", System.currentTimeMillis() + "");
            jedis.hmset(token, map);

            resultMap.put("token",token);
            resultMap.put("nickName", user.getNickName());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_LOGIN); //用户登陆失败
        } finally {
            jedis.close();
        }
        return resultMap;
    }
}
