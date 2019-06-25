package cn.com.cybertech.service.impl;

import cn.com.cybertech.config.redis.RedisTool;
import cn.com.cybertech.dao.SysUserMapper;
import cn.com.cybertech.dao.WebCompanyMapper;
import cn.com.cybertech.model.SysUser;
import cn.com.cybertech.service.SysUserService;
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

@Service("sysUserService")
public class SysUserServiceImpl implements SysUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private WebCompanyMapper webCompanyMapper;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisTool redisTool;

    @Override
    public List<SysUser> getSysUserList(String token, SysUser sysUser) {
        return sysUserMapper.getList(sysUser);
    }

    @Override
    public SysUser getSysUserByUserName(String userName) {
        return sysUserMapper.getSysUserByUserName(userName);
    }

    @Override
    public Map<String, Object> login(SysUser sysUser, String platform) {
        SysUser user = sysUserMapper.getSysUserByUserName(sysUser.getUserName());
        Map<String, Object> resultMap = Maps.newHashMap();
        if (user == null) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT); //用户不存在
        }

        String newPwd = EncryptUtils.MD5Encode(sysUser.getUserName() + sysUser.getPassword() + "*!!");
        System.out.println(newPwd);
        if (!newPwd.equals(user.getPassword())) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_PASSWORD); //用户不存在
        }

        if (user.getState() == 0) {
            throw new ValueRuntimeException(MessageCode.USERINFO_DISABLE); //用户已被禁用
        }
        //生成token
        String token = EncryptUtils.MD5Encode(platform + sysUser.getUserName());

        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        try {
            Map<String, String> map = Maps.newHashMap();
            map.put("userId", user.getId() + "");
            map.put("userName", user.getUserName());
            map.put("nickName", user.getNickName());
            map.put("roleId", user.getRoleId() + "");
            map.put("source", CodeUtil.USER_TYPE_SYS);
            map.put("timestamp", System.currentTimeMillis() + "");
            jedis.hmset(CodeUtil.REDIS_PREFIX + token, map);

            resultMap.put("token", token);
            resultMap.put("nickName", user.getNickName());
            resultMap.put("userId", user.getId());
            resultMap.put("createTime", user.getCreateTimeStr());
            resultMap.put("roleId", user.getRoleId());
            resultMap.put("source", CodeUtil.USER_TYPE_SYS);
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
    public void addOrEditUser(String token, String platform, SysUser sysUser) {
        int count = 0;
        if (sysUser.getId() == null) {  //新增
            if (StringUtils.isBlank(sysUser.getUserName()) || StringUtils.isBlank(sysUser.getPassword())) {
                throw new ValueRuntimeException(MessageCode.USERINFO_PARAM_NULL);
            }
            SysUser user = sysUserMapper.getSysUserByUserName(sysUser.getUserName());
            if (user != null) {
                throw new ValueRuntimeException(MessageCode.USERINFO_EXIST);
            }
            String newPwd = EncryptUtils.MD5Encode(sysUser.getUserName() + sysUser.getPassword() + "*!!");
            if (user != null) {
                if (StringUtils.isNotBlank(user.getPassword())) {
                    newPwd = user.getPassword();
                }
            }
            sysUser.setPassword(newPwd);
            count = sysUserMapper.insertSysUser(sysUser);
        } else {  //编辑
            count = sysUserMapper.updateSysUser(sysUser);
            clearUserSession(platform, sysUser);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_ADD);
        }
    }

    @Override
    @Transactional
    public void optionUser(String platform, Long userId, Integer state) {
        SysUser user = sysUserMapper.getSysUserById(userId);
        if (state == -1) {  //删除
            int count = sysUserMapper.deleteSysUserById(userId);
            if (count == 0) {
                throw new ValueRuntimeException(MessageCode.USERINFO_ERR_DEL);
            }
        } else {   //禁用/启用
            user.setState(state);
            int count = sysUserMapper.updateSysUser(user);
            if (count == 0) {
                throw new ValueRuntimeException(MessageCode.USERINFO_ERR_ADD);
            }
        }
        clearUserSession(platform, user);
    }

    private void clearUserSession(String platform, SysUser user) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        try {
            String token = EncryptUtils.MD5Encode(platform + user.getUserName());
            jedis.del(CodeUtil.REDIS_PREFIX + token); //清除用户session，重新登陆
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_LOGIN); //用户登陆失败
        } finally {
            jedis.close();
        }
    }

    @Override
    public SysUser getUserInfo(String token) {
        SysUser localUser = redisTool.getSysUser(CodeUtil.REDIS_PREFIX + token);
        SysUser user = sysUserMapper.getSysUserById(localUser.getId());
        return user;
    }

    @Override
    public void resetPassword(String token, String oldPassword, String newPassword) {
        SysUser localUser = redisTool.getSysUser(CodeUtil.REDIS_PREFIX + token);
        SysUser user = sysUserMapper.getSysUserById(localUser.getId());
        String localoldPwd = EncryptUtils.MD5Encode(user.getUserName() + oldPassword + "*!!");
        if (!localoldPwd.equals(user.getPassword())) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_OLDPASS); //原密码错误
        }

        String newPwd = EncryptUtils.MD5Encode(user.getUserName() + newPassword + "*!!");
        user.setPassword(newPwd);
        int count = sysUserMapper.updateSysUserPassByUserName(newPwd, user.getUserName());
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_RESETPASS); //修改密码失败
        }
    }

    public static void main(String[] args) {
        String newPwd = EncryptUtils.MD5Encode("admin" + "111111" + "*!!");
        System.out.println(newPwd);
    }
}
