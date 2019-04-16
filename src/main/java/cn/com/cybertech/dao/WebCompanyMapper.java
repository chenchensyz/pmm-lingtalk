package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.WebCompany;

import java.util.List;

public interface WebCompanyMapper extends BaseDao<WebCompany> {

    List<WebCompany> selectWebCompanyByPhone(String phone);
}