package cn.com.cybertech.service;

import cn.com.cybertech.model.AppCert;

import java.util.List;
import java.util.Map;

public interface AppCertService {

    List<Map<String,Object>> getApiAppCertList();

    int insertAppCert(AppCert appCert);

    int updateAppCert(AppCert appCert);

    List<AppCert> getAppCertList(AppCert appCert);
}