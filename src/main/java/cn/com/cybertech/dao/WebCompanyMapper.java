package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.WebCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WebCompanyMapper extends BaseDao<WebCompany> {

    List<WebCompany> getWebCompanyByUserName(String userName);

   int checkWebCompanyByOwner(@Param("id") Integer id,@Param("owner") String owner);
}