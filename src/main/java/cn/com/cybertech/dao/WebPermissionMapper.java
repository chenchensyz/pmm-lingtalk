package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.WebPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WebPermissionMapper extends BaseDao<WebPermission> {

    List<WebPermission> getPermByUserId(@Param("userId") Integer userId, @Param("types") List<Integer> types);

    List<Integer> getPermsByRoleId(Integer roleId);

}