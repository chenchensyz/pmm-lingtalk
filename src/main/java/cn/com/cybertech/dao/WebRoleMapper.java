package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.WebRole;

import java.util.List;

public interface WebRoleMapper extends BaseDao<WebRole> {

    List<WebRole> getCompanyRoleList();
}