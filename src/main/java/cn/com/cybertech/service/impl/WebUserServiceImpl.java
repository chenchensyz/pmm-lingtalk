package cn.com.cybertech.service.impl;

import cn.com.cybertech.config.redis.RedisTool;
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

    @Autowired
    private RedisTool redisTool;

    @Override
    public List<WebUser> getWebUserList(String token, WebUser webUser) {
        WebUser user = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
        webUser.setCompanyId(user.getCompanyId());
        return webUserMapper.getList(webUser);
    }

    @Override
    public WebUser getWebUserLoginPass(String userName) {
        return webUserMapper.getWebUserLoginPass(userName);
    }

    @Override
    public Map<String, Object> login(WebUser webUser, String platform) {
        WebUser user = webUserMapper.getWebUserLogin(webUser.getUserName(), webUser.getCompanyId());
        Map<String, Object> resultMap = Maps.newHashMap();
        if (user == null) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT); //用户不存在
        }

        String newPwd = EncryptUtils.MD5Encode(webUser.getUserName() + webUser.getPassword() + "*!!");
        if (!newPwd.equals(user.getPassword())) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_PASSWORD); //用户不存在
        }

        if (user.getState() == 0) {
            throw new ValueRuntimeException(MessageCode.USERINFO_DISABLE); //用户已被禁用
        }
        //生成token
        String token = EncryptUtils.MD5Encode(platform + webUser.getUserName() + webUser.getCompanyId());

        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        try {
            Map<String, String> map = Maps.newHashMap();
            map.put("userId", user.getId() + "");
            map.put("userName", user.getUserName());
            map.put("nickName", user.getNickName());
            map.put("companyId", user.getCompanyId() + "");
            map.put("roleId", user.getRoleId() + "");
            map.put("source", CodeUtil.USER_TYPE_WEB);
            map.put("timestamp", System.currentTimeMillis() + "");
            jedis.hmset(CodeUtil.REDIS_PREFIX + token, map);

            resultMap.put("token", token);
            resultMap.put("nickName", user.getNickName());
            resultMap.put("userId", user.getId());
            resultMap.put("companyId", user.getCompanyId());
            int owner = webCompanyMapper.checkWebCompanyByOwner(user.getCompanyId(), user.getUserName());
            resultMap.put("owner", owner); //是否当前的公司管理员
            resultMap.put("createTime", user.getCreateTimeStr());
            resultMap.put("roleId", user.getRoleId());
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
        webCompany.setOwner(webUser.getUserName());
        int count1 = webCompanyMapper.insertSelective(webCompany);
        WebUser user = webUserMapper.getWebUserLoginPass(webUser.getUserName());
        String newPwd;
        if (user != null && StringUtils.isNotBlank(user.getPassword())) {
            newPwd = user.getPassword();
        } else {
            newPwd = EncryptUtils.MD5Encode(webUser.getUserName() + webUser.getPassword() + "*!!");
        }
        webUser.setPassword(newPwd);
        webUser.setCompanyId(webCompany.getId());
        webUser.setRoleId(CodeUtil.ROLE_COMPANY_MANAGER);
        int count2 = webUserMapper.insertWebUser(webUser);
        if (user == null) {
            int count3 = webUserMapper.insertWebUserLogin(webUser);
            if (count3 == 0) {
                throw new ValueRuntimeException(MessageCode.USERINFO_DETAIL_ERR_ADD);
            }
        }
        if (count1 + count2 < 2) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_ADD);
        }
    }

    @Override
    public String getUserAppByAppId(Integer appId) {
        return webUserMapper.getUserAppByAppId(appId);
    }

    @Override
    @Transactional
    public void addOrEditUser(String token, String platform, WebUser webUser) {
        int count = 0;
        WebUser localUser = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
        webUser.setCompanyId(localUser.getCompanyId());
        List<Integer> appCheckedList = webUser.getAppCheckedList();
        if (webUser.getId() == null) {  //新增
            WebUser valiedUser = webUserMapper.getWebUserLogin(webUser.getUserName(), webUser.getCompanyId());
            if (valiedUser != null) { //当前公司用户已存在
                throw new ValueRuntimeException(MessageCode.USERINFO_EXIST);
            }
            WebUser user = webUserMapper.getWebUserLoginPass(webUser.getUserName());
            if (user == null && StringUtils.isBlank(webUser.getPassword())) { //密码为空，可能用户已存在
                throw new ValueRuntimeException(MessageCode.USERINFO_PARAM_NULL);
            }
            if (user != null && StringUtils.isNotBlank(user.getPassword())) {
                webUser.setPassword(user.getPassword());
            } else {  //保存用户登陆信息
                String newPwd = EncryptUtils.MD5Encode(webUser.getUserName() + webUser.getPassword() + "*!!");
                webUser.setPassword(newPwd);
                int userLogin = webUserMapper.insertWebUserLogin(webUser);
                if (userLogin == 0) {
                    throw new ValueRuntimeException(MessageCode.USERINFO_DETAIL_ERR_ADD);
                }
            }
            if (localUser.getRoleId() == CodeUtil.ROLE_COMPANY_MANAGER) {
                webUser.setRoleId(CodeUtil.ROLE_COMPANY_DEVELOPER);
                appCheckedList = webUserMapper.getUserApp(localUser.getId(), localUser.getCompanyId());
            }
            count = webUserMapper.insertWebUser(webUser);
        } else {  //编辑
            List<Integer> userApp = webUserMapper.getUserApp(webUser.getId(), webUser.getCompanyId());
            if (userApp != null && userApp.size() > 0) {
                webUserMapper.deleteUserApp(webUser.getId(), webUser.getCompanyId());
            }
            count = webUserMapper.updateWebUser(webUser);
            clearUserSession(platform, webUser);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_ADD);
        }
        if (appCheckedList != null && appCheckedList.size() > 0) {
            webUserMapper.insertUserApp(webUser.getId(), webUser.getCompanyId(), appCheckedList);
        }
    }

    @Override
    public List<Integer> getUserApp(String token, Long userId) {
        WebUser user = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
        List<Integer> userApp = webUserMapper.getUserApp(userId, user.getCompanyId());
        return userApp;
    }

    @Override
    @Transactional
    public void optionUser(String platform, Long userId, Integer state) {
        WebUser user = webUserMapper.getWebUserById(userId);
        if (state == -1) {  //删除
            webUserMapper.deleteUserApp(userId, user.getCompanyId());
            int count = webUserMapper.deleteWebUserById(userId);
            if (count == 0) {
                throw new ValueRuntimeException(MessageCode.USERINFO_ERR_DEL);
            }
        } else {   //禁用/启用
            user.setState(state);
            int count = webUserMapper.updateWebUser(user);
            if (count == 0) {
                throw new ValueRuntimeException(MessageCode.USERINFO_ERR_ADD);
            }
        }
        clearUserSession(platform, user);
    }

    private void clearUserSession(String platform, WebUser user) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        try {
            String token = EncryptUtils.MD5Encode(platform + user.getUserName() + user.getCompanyId());
            jedis.del(CodeUtil.REDIS_PREFIX + token); //清除用户session，重新登陆
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_LOGIN); //用户登陆失败
        } finally {
            jedis.close();
        }
    }

    @Override
    public WebUser getUserInfo(String token) {
        WebUser localUser = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
        WebUser user = webUserMapper.getWebUserById(localUser.getId());
        return user;
    }

    @Override
    public void resetPassword(String token, String oldPassword, String newPassword) {
        WebUser localUser = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
        WebUser user = webUserMapper.getWebUserLoginPass(localUser.getUserName());
        String localoldPwd = EncryptUtils.MD5Encode(user.getUserName() + oldPassword + "*!!");
        if (!localoldPwd.equals(user.getPassword())) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_OLDPASS); //原密码错误
        }

        String newPwd = EncryptUtils.MD5Encode(user.getUserName() + newPassword + "*!!");
        user.setPassword(newPwd);
        int count = webUserMapper.updateWebUserLogin(user);
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_RESETPASS); //修改密码失败
        }

    }
}
