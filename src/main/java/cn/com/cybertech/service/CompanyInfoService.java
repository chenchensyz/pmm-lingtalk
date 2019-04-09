package cn.com.cybertech.service;

import cn.com.cybertech.model.CompanyInfo;

import java.util.List;

public interface CompanyInfoService{

    List<CompanyInfo> selectCompanyByPhone(String phone);
}