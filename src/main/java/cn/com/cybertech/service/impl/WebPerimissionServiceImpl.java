package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.WebPermissionMapper;
import cn.com.cybertech.model.WebPermission;
import cn.com.cybertech.service.WebPerimissionService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("webPerimissionService")
public class WebPerimissionServiceImpl implements WebPerimissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebPerimissionServiceImpl.class);

    @Autowired
    private WebPermissionMapper webPermissionMapper;

    @Override
    public List<WebPermission> getPermissions(Integer roleId, List<Integer> types, String source) {
        List<WebPermission> permissions = new ArrayList<>();
        List<WebPermission> menus;
        if (roleId != null) { //角色用户拥有的权限
            menus = webPermissionMapper.getPermByRoleId(roleId, types);
        } else {
            menus = webPermissionMapper.getPermList(source);
        }
        for (WebPermission menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                menu.setLevel(0);
                if (!exists(permissions, menu)) {
                    permissions.add(menu);
                }
            }
        }
        permissions.sort((o1, o2) -> o1.getOrderNum().compareTo(o2.getOrderNum()));
        findChildren(permissions, menus);
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

    private void findChildren(List<WebPermission> permissions, List<WebPermission> menus) {
        for (WebPermission permission : permissions) {
            List<WebPermission> children = new ArrayList<>();
            for (WebPermission menu : menus) {
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
            findChildren(children, menus);
        }
    }

    @Override
    public Set<String> findPermissions(String userName) {
        Set<String> perms = new HashSet<>();
        List<WebPermission> permissions = webPermissionMapper.getPermList(null);
        for (WebPermission permission : permissions) {
            if (permission.getPerms() != null && !"".equals(permission.getPerms())) {
                perms.add(permission.getPerms());
            }
        }
        return perms;
    }

    //根据角色查询拥有的权限
    @Override
    public List<WebPermission> getPermsByRoleId(Integer roleId) {
        List<WebPermission> perms = webPermissionMapper.getPermsByRoleId(roleId);
        return perms;
    }

    @Override
    @Transactional
    public void addOrEditPerm(WebPermission webPermission) {
        int count;
        if (webPermission.getId() == null) { //新增权限或子节点
            if (webPermission.getParentId() > 0 || webPermission.getType() == 2) {  //新增子节点
                int maxOrderNum = webPermissionMapper.getMaxOrderNum(webPermission.getParentId(), webPermission.getType());
                webPermission.setOrderNum(maxOrderNum + 1);
            } else {
                webPermission.setOrderNum(1);
            }
            webPermission.setCreateTime(new Date());
            count = webPermissionMapper.insertWebPermission(webPermission);
        } else {
            webPermission.setUpdateTime(new Date());
            count = webPermissionMapper.updateWebPermission(webPermission);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.PERM_ERR_SAVE);
        }
    }

    @Override
    @Transactional
    public void deletePerm(String ids) {
        List<Integer> idList = Lists.newArrayList();
        String[] idArray = ids.split(",");
        for (int i = 0; i < idArray.length; i++) {
            idList.add(Integer.valueOf(idArray[i]));
        }
        int count = webPermissionMapper.deleteWebPermInIds(idList);
        if (count != idArray.length) {
            throw new ValueRuntimeException(MessageCode.PERM_ERR_DEL);
        }
    }
}
