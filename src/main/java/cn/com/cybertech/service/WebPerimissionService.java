package cn.com.cybertech.service;

import cn.com.cybertech.model.WebPermission;

import java.util.List;
import java.util.Set;

public interface WebPerimissionService {

    List<WebPermission> getPermissions(String userName, Integer menuType);

    Set<String> findPermissions(String userName);
}
