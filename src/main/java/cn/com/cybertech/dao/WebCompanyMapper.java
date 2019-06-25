package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.WebCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WebCompanyMapper{

    List<WebCompany> getWebCompanyList(WebCompany webCompany);

    List<WebCompany> getWebCompanyByUserName(String userName);

    int checkWebCompanyByOwner(@Param("id") Integer id, @Param("owner") String owner);

    int insertWebCompany(WebCompany webCompany);

    int updateWebCompany(WebCompany webCompany);

    WebCompany getWebCompanyById(Integer id);

    int deleteWebCompanyById(Integer id);
}