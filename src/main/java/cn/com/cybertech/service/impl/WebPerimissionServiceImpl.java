package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.WebPermissionMapper;
import cn.com.cybertech.model.WebPermission;
import cn.com.cybertech.service.WebPerimissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("webPerimissionService")
public class WebPerimissionServiceImpl implements WebPerimissionService {

    @Autowired
    private WebPermissionMapper webPermissionMapper;

    @Override
    public List<WebPermission> getPermissions(String userName, Integer menuType) {
        List<WebPermission> permissions = new ArrayList<>();
        List<WebPermission> menus = webPermissionMapper.getList(new WebPermission());
        for (WebPermission menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                menu.setLevel(0);
                if (!exists(permissions, menu)) {
                    permissions.add(menu);
                }
            }
        }
        permissions.sort((o1, o2) -> o1.getOrderNum().compareTo(o2.getOrderNum()));
        findChildren(permissions, menus, menuType);
        return permissions;
    }


    private boolean exists(List<WebPermission> permissions, WebPermission menu) {
        boolean exist = false;
        for (WebPermission permission : permissions) {
            if (menu.getId() == permission.getId()) {
                exist = true;
            }
        }
        return exist;
    }

    private void findChildren(List<WebPermission> permissions, List<WebPermission> menus, int menuType) {
        for (WebPermission permission : permissions) {
            List<WebPermission> children = new ArrayList<>();
            for (WebPermission menu : menus) {
                if (menuType == 1 && menu.getType() == 2) {
                    // 如果是获取类型不需要按钮，且菜单类型是按钮的，直接过滤掉
                    continue;
                }
                if (permission.getId() != null && permission.getId() == menu.getParentId()) {
                    menu.setParentName(permission.getName());
                    menu.setLevel(permission.getLevel() + 1);
                    if (!exists(children, menu)) {
                        children.add(menu);
                    }
                }
            }
            permission.setChildren(children);
            children.sort((o1, o2) -> o1.getOrderNum().compareTo(o2.getOrderNum()));
            findChildren(children, menus, menuType);
        }
    }

    @Override
    public Set<String> findPermissions(String userName) {
        Set<String> perms = new HashSet<>();
        List<WebPermission> permissions = webPermissionMapper.getList(new WebPermission());
        for (WebPermission permission : permissions) {
            if (permission.getPerms() != null && !"".equals(permission.getPerms())) {
                perms.add(permission.getPerms());
            }
        }
        return perms;
    }

}
