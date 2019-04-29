package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.WebRoleMapper;
import cn.com.cybertech.model.WebRole;
import cn.com.cybertech.service.WebRoleService;
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

    @Override
    public List<WebRole> getRoleList(WebRole webRole) {
        return webRoleMapper.getList(webRole);
    }

    @Override
    public List<WebRole> getCompanyRoleList() {
        return webRoleMapper.getCompanyRoleList();
    }

    @Override
    @Transactional
    public void addOrEdidRole(WebRole webRole) {
        if (webRole.getId() != null) { //编辑角色
            int update = webRoleMapper.updateWebRole(webRole);
            if (update == 0) {
                throw new ValueRuntimeException(MessageCode.ROLE_ERR_UPDATE);
            }
            //清空原有角色权限对应关系
            webRoleMapper.deleteRolePerm(webRole.getId());
        } else { //新增角色
            int insert = webRoleMapper.insertSelective(webRole);
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
}