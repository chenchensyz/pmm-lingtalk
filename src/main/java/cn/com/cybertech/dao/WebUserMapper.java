package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.WebUser;
import org.apache.ibatis.annotations.Param;

public interface WebUserMapper extends BaseDao<WebUser> {

    WebUser getWebLoginByPhone(@Param("phone") String phone, @Param("companyId") Integer companyId);

}