package cn.com.cybertech.dao;

import cn.com.cybertech.model.WebUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WebUserMapper {

    List<WebUser> getList(WebUser webUser);

    int insertWebUser(WebUser webUser);

    int updateWebUser(WebUser webUser);

    WebUser getWebUserById(Long id);

    WebUser getWebUserByPhone(@Param("phone") String phone, @Param("companyId") Integer companyId);

    //查询：用户绑定应用
    List<Integer> getUserApp(@Param("userId") Long userId, @Param("companyId") Integer companyId);

    //新增：用户绑定应用
    int insertUserApp(@Param("userId") Long userId, @Param("companyId") Integer companyId, @Param("appIds") List<Integer> appIds);

    //删除：用户绑定应用
    int deleteUserApp(@Param("userId") Long userId, @Param("companyId") Integer companyId);

    //根据角色查询用户
    int getWebUserByRoleId(Integer roleId);

    int  updateUserPassByPhone(@Param("password") String password, @Param("phone") String phone);

}