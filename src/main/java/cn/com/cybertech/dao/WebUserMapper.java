package cn.com.cybertech.dao;

import cn.com.cybertech.model.WebUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WebUserMapper {

    List<WebUser> getList(WebUser webUser);

   int insertWebUser(WebUser webUser);

    int updateWebUser(WebUser webUser);

    WebUser getWebUserByPhone(@Param("phone") String phone, @Param("companyId") Integer companyId);

}