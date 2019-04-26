package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.WebRoleMapper;
import cn.com.cybertech.model.WebRole;
import cn.com.cybertech.service.WebRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}