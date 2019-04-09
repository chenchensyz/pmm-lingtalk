package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.CompanyInfo;

import java.util.List;

public interface CompanyInfoMapper extends BaseDao<CompanyInfo> {

    List<CompanyInfo> selectCompanyByPhone(String phone);
}