package cn.com.cybertech.service;

import cn.com.cybertech.model.WebCompany;

import java.util.List;

public interface WebCompanyService {

    void saveWebCompany(String token,WebCompany webCompany);

    List<WebCompany> getWebCompanyByUserName(String userName);

    List<WebCompany> getWebCompanyList(WebCompany webCompany);

    void changeCompany(Integer id, Integer state);
}