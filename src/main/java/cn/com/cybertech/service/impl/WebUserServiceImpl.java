package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.WebCompanyMapper;
import cn.com.cybertech.dao.WebUserMapper;
import cn.com.cybertech.model.WebCompany;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebUserService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.EncryptUtils;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private WebCompanyMapper webCompanyMapper;

    @Autowired
    private JedisPool jedisPool;

    @Override
    public List<WebUser> getWebUserList(WebUser webUser) {
        return webUserMapper.getList(webUser);
    }

    @Override
    public WebUser getWebUserByPhone(String phone, Integer companyId) {
        return webUserMapper.getWebUserByPhone(phone, companyId);
    }

    @Override
    public Map<String, Object> login(WebUser webUser, String platform) {
        WebUser user = webUserMapper.getWebUserByPhone(webUser.getPhone(), webUser.getCompanyId());
        Map<String, Object> resultMap = Maps.newHashMap();
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
            map.put("userId", user.getId() + "");
            map.put("phone", user.getPhone());
            map.put("nickName", user.getNickName());
            map.put("companyId", user.getCompanyId() + "");
            map.put("timestamp", System.currentTimeMillis() + "");
            jedis.hmset(CodeUtil.REDIS_PREFIX + token, map);

            resultMap.put("token", token);
            resultMap.put("nickName", user.getNickName());
            resultMap.put("userId", user.getId());
            resultMap.put("companyId", user.getCompanyId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_LOGIN); //用户登陆失败
        } finally {
            jedis.close();
        }
        return resultMap;
    }

    @Override
    @Transactional
    public void registerUser(WebUser webUser, String companyName, String introduction) {
        WebCompany webCompany = new WebCompany();
        webCompany.setCompanyName(companyName);
        webCompany.setIntroduction(introduction);
        int count1 = webCompanyMapper.insertSelective(webCompany);
        WebUser user = webUserMapper.getWebUserByPhone(webUser.getPhone(), null);
        String newPwd;
        if (user != null && StringUtils.isNotBlank(user.getPassword())) {
            newPwd = user.getPassword();
        } else {
            newPwd = EncryptUtils.MD5Encode(webUser.getPhone() + webUser.getPassword() + "*!!");
        }
        webUser.setPassword(newPwd);
        webUser.setCompanyId(webCompany.getId());
        webUser.setRoleId(CodeUtil.ROLE_COMPANY_MANAGER);
        int count2 = webUserMapper.insertWebUser(webUser);
        if (count1 + count2 < 2) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_ADD);
        }
    }

    @Override
    @Transactional
    public void addOrEdidUser(WebUser webUser) {
        int count = 0;
        if (webUser.getId() == null) {  //新增
            if (StringUtils.isBlank(webUser.getPhone()) || StringUtils.isBlank(webUser.getPassword())) {
                throw new ValueRuntimeException(MessageCode.USERINFO_PARAM_NULL);
            }
            WebUser user = webUserMapper.getWebUserByPhone(webUser.getPhone(), null);
            String newPwd = EncryptUtils.MD5Encode(webUser.getPhone() + webUser.getPassword() + "*!!");
            if (user != null) {
                if (user.getCompanyId() == webUser.getCompanyId()) {
                    throw new ValueRuntimeException(MessageCode.USERINFO_EXIST);
                }
                if (StringUtils.isNotBlank(user.getPassword())) {
                    newPwd = user.getPassword();
                }
            }
            webUser.setPassword(newPwd);
            count = webUserMapper.insertWebUser(webUser);
        } else {  //编辑
            count = webUserMapper.updateWebUser(webUser);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_ADD);
        }
    }
}
