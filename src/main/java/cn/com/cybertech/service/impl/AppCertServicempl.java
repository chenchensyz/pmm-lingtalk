package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppCertMapper;
import cn.com.cybertech.model.AppCert;
import cn.com.cybertech.service.AppCertService;
import cn.com.cybertech.tools.CodeUtil;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("appCertService")
public class AppCertServicempl implements AppCertService {

    @Autowired
    private AppCertMapper appCertMapper;

    @Override
    public List<Map<String, Object>> getApiAppCertList() {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        List<AppCert> appCerts = appCertMapper.getList(new AppCert());
        if (appCerts != null && appCerts.size() > 0) {
            for (AppCert appCert : appCerts) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("pushtype", appCert.getType());
                if (CodeUtil.CERT_IOS.equals(appCert.getType())) { //苹果证书
                    map.put("bundleid", appCert.getCertId());
                    String cert = appCert.getCertFile() == null ? "" : Base64.encodeBase64String(appCert.getCertFile());
                    String key = appCert.getCertFile() == null ? "" : Base64.encodeBase64String(appCert.getKeyFile());
                    map.put("cert", cert);
                    map.put("key", key);
                    map.put("pass", Base64.encodeBase64String(appCert.getCertSecret().getBytes(CodeUtil.cs)));
                    map.put("production", true);
                } else {  //Android证书
                    map.put("appid", appCert.getCertId());
                    map.put("secret", appCert.getCertSecret());
                    map.put("apkname", appCert.getApkName());
                }
                resultList.add(map);
            }
        }
        return resultList;
    }

    @Override
    public int insertAppCert(AppCert appCert) {
        return appCertMapper.insertAppCert(appCert);
    }

    @Override
    public int updateAppCert(AppCert appCert) {
        return appCertMapper.updateAppCert(appCert);
    }

    @Override
    public List<AppCert> getAppCertList(AppCert appCert) {
        List<AppCert> appCerts = appCertMapper.getAppCertList(appCert);
        return appCerts;
    }
}