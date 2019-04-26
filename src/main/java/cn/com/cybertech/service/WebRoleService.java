package cn.com.cybertech.service;

import cn.com.cybertech.model.WebRole;

import java.util.List;
import java.util.Set;

public interface WebRoleService {

    List<WebRole> getRoleList(WebRole webRole);

    List<WebRole> getCompanyRoleList();

}
