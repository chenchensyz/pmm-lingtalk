package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.AppCert;

import java.util.List;

public interface AppCertMapper extends BaseDao<AppCert> {

    int insertAppCert(AppCert appCert);

    int updateAppCert(AppCert appCert);

    List<AppCert> getAppCertList(AppCert appCert);
}