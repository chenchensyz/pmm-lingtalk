package cn.com.cybertech.service.impl;

import cn.com.cybertech.config.redis.RedisTool;
import cn.com.cybertech.dao.WebRoleMapper;
import cn.com.cybertech.dao.WebUserMapper;
import cn.com.cybertech.model.WebRole;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebRoleService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("webRoleService")
public class WebRoleServiceImpl implements WebRoleService {

    @Autowired
    private WebRoleMapper webRoleMapper;

    @Autowired
    private WebUserMapper webUserMapper;

    @Autowired
    private RedisTool redisTool;

    @Override
    public List<WebRole> getRoleList(WebRole webRole) {
        return webRoleMapper.getRoleList(webRole);
    }

    @Override
    public List<WebRole> getCompanyRoleList(String token) {
        WebUser webUser = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
        return webRoleMapper.getCompanyRoleList(webUser.getSource());
    }

    @Override
    @Transactional
    public void addOrEdidRole(WebRole webRole) {
        if (webRole.getId() != null) { //编辑角色
            WebRole role = webRoleMapper.getWebRoleByCode(webRole.getRoleCode());
            if (role != null && role.getId() != webRole.getId()) { //判断角色编码是否重复
                throw new ValueRuntimeException(MessageCode.ROLE_EXIT_CODE);
            }
            int update = webRoleMapper.updateWebRole(webRole);
            if (update == 0) {
                throw new ValueRuntimeException(MessageCode.ROLE_ERR_UPDATE);
            }
            //清空原有角色权限对应关系
            webRoleMapper.deleteRolePerm(webRole.getId());
        } else { //新增角色
            int insert = webRoleMapper.insertWebRole(webRole);
            if (insert == 0) {
                throw new ValueRuntimeException(MessageCode.ROLE_ERR_ADD);
            }
        }
        //新增角色权限对应关系
        int addRolePerm = webRoleMapper.addRolePerm(webRole);
        if (addRolePerm == 0) {
            throw new ValueRuntimeException(MessageCode.ROLE_PERM_ERR_ADD);
        }
    }

    @Override
    @Transactional
    public void deleteRole(Integer roleId) {
        if (webUserMapper.getWebUserByRoleId(roleId) > 0) {  //根据角色查询用户
            throw new ValueRuntimeException(MessageCode.ROLE_USED_ING); //当前角色已被使用，请先解除使用
        }
        int count = webRoleMapper.deleteRolePerm(roleId);  //删除角色权限对应关系
        int count2 = webRoleMapper.deleteRoleById(roleId); //删除角色
        if (count + count2 < 2) {
            throw new ValueRuntimeException(MessageCode.ROLE_ERR_DELETE);
        }

    }
}