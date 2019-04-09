package cn.com.cybertech.service;

import cn.com.cybertech.model.WebRole;

import java.util.List;
import java.util.Set;

public interface WebRoleService {

    List<WebRole> getPermissions(String userName, Integer menuType);

    Set<String> findPermissions(String userName);
}
