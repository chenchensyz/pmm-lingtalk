package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.WebRole;

import java.util.List;

public interface WebRoleMapper extends BaseDao<WebRole> {

    int updateWebRole(WebRole webRole);

    List<WebRole> getCompanyRoleList();

    int deleteRolePerm(Integer roleId);

    int addRolePerm(WebRole webRole);
}