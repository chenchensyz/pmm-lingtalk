package cn.com.cybertech.service;

import cn.com.cybertech.model.WebCompany;

import java.util.List;

public interface WebCompanyService{

    void saveWebCompany(WebCompany webCompany);

    List<WebCompany> getWebCompanyByUserName(String userName);

    List<WebCompany> getWebCompanyList(WebCompany webCompany);
}