package cn.com.cybertech.service.impl;

import cn.com.cybertech.config.redis.RedisTool;
import cn.com.cybertech.dao.AppCertMapper;
import cn.com.cybertech.model.AppCert;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.AppCertService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.FileConvertUtils;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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
        if (appCerts != null && appCerts.size() > 0) {
            for (AppCert cert : appCerts) {
                if (cert.getCertFile() != null && cert.getKeyFile() != null) {
                    cert.setCertUpload(true);
                }
            }
        }
        return appCerts;
    }

    @Override
    public void addOrEditAppCert(HttpServletRequest request, AppCert appCert) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        List<MultipartFile> keys = ((MultipartHttpServletRequest) request).getFiles("key");
        if (files != null && files.size() > 0) { //保存ios证书密钥文件
            MultipartFile file = files.get(0);
            appCert.setKeyFile(getBytes(appCert, file, "file"));
        }
        if (keys != null && keys.size() > 0) { //保存key密钥文件
            MultipartFile key = keys.get(0);
            appCert.setCertFile(getBytes(appCert, key, "key"));
        }
        int count;
        if (appCert.getId() != null) {
            count = appCertMapper.updateAppCert(appCert);
        } else {
            count = appCertMapper.insertAppCert(appCert);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.CERT_ERR_SAVE);
        }

    }

    private byte[] getBytes(AppCert appCert, MultipartFile file, String fileKey) {
        String fileName = appCert.getAppId() + "#" + fileKey;
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String filePath = "D:\\" + File.separator + fileName + suffix;
        File dest = new File(filePath);
        if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
            dest.getParentFile().mkdir();
        }
        try {
            file.transferTo(dest);
            return FileConvertUtils.getBytes(null, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int deleteAppCert(Long certId) {
        return appCertMapper.deleteByPrimaryKey(certId);
    }
}