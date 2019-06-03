package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppCertMapper;
import cn.com.cybertech.model.AppCert;
import cn.com.cybertech.service.AppCertService;
import cn.com.cybertech.tools.*;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service("appCertService")
public class AppCertServicempl implements AppCertService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppCertServicempl.class);

    @Autowired
    private AppCertMapper appCertMapper;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private Environment env;

    private String fileSuffix;

    @Override
    public List<Map<String, Object>> getApiAppCertList() {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        List<AppCert> appCerts = appCertMapper.getList(new AppCert());
        if (appCerts != null && appCerts.size() > 0) {
            for (AppCert appCert : appCerts) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("pushtype", appCert.getType());
                String type = messageCodeUtil.getMessage(CodeUtil.CERT_IOS);
                if (type.equals(appCert.getType())) { //苹果证书
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
    @Transactional
    public void addOrEditAppCert(HttpServletRequest request, AppCert appCert) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        List<MultipartFile> keys = ((MultipartHttpServletRequest) request).getFiles("key");
        if (files != null && files.size() > 0) { //保存ios证书密钥文件
            MultipartFile file = files.get(0);
            appCert.setCertFile(getBytes(appCert, file, CodeUtil.CERT_TYPE_FILE));
        }
        if (keys != null && keys.size() > 0) { //保存key密钥文件
            MultipartFile key = keys.get(0);
            appCert.setKeyFile(getBytes(appCert, key, CodeUtil.CERT_TYPE_KEY));
        }
        appCert.setCertSuffix(fileSuffix);
        int count;
        if (appCert.getId() != null) {
            count = appCertMapper.updateAppCert(appCert);
        } else {
            count = appCertMapper.insertAppCert(appCert);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.CERT_ERR_SAVE);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pushtype", appCert.getType());
        String type = messageCodeUtil.getMessage(CodeUtil.CERT_IOS);
        String upload_url = env.getProperty(CodeUtil.CERT_PROD_UPLOAD_URL);
        if (type.equals(appCert.getType())) { //苹果证书
            jsonObject.put("bundleid", appCert.getCertId());
            String cert = appCert.getCertFile() == null ? "" : Base64.encodeBase64String(appCert.getCertFile());
            String key = appCert.getCertFile() == null ? "" : Base64.encodeBase64String(appCert.getKeyFile());
            jsonObject.put("cert", cert);
            jsonObject.put("key", key);
            jsonObject.put("pass", Base64.encodeBase64String(appCert.getCertSecret().getBytes(CodeUtil.cs)));
            if(appCert.getCertEnviron() == 0){
                upload_url = env.getProperty(CodeUtil.CERT_UPLOAD_URL);  //测试环境
                jsonObject.put("production", false);
            }else {
                jsonObject.put("production", true);
            }
        } else {  //Android证书
            jsonObject.put("appid", appCert.getCertId());
            jsonObject.put("secret", appCert.getCertSecret());
            jsonObject.put("apkname", appCert.getApkName());
        }
        String cert_change_url = env.getProperty(CodeUtil.CERT_CHANGE_URL);
        LOGGER.info("发送证书：{}", jsonObject);
        HttpClientUtil.httpRequest(upload_url + cert_change_url, CodeUtil.METHOD_POST, CodeUtil.CONTEXT_JSON, jsonObject.toString());
    }

    private byte[] getBytes(AppCert appCert, MultipartFile file, String fileKey) {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = getNewFileName(appCert, fileKey, suffix);
        fileSuffix = suffix;
        String filePath = env.getProperty(CodeUtil.CERT_SAVE_PATH) + File.separator + fileName;
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

    public void deleteFile(AppCert appCert, String fileKey) {
        String fileName = getNewFileName(appCert, fileKey, appCert.getCertSuffix());
        String filePath = env.getProperty(CodeUtil.CERT_SAVE_PATH) + File.separator + fileName;
        File file = new File(filePath);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (!file.delete()) {
                if (fileKey.equals(CodeUtil.CERT_TYPE_FILE)) {
                    throw new ValueRuntimeException(MessageCode.CERT_FILE_ERR_DEL);
                } else {
                    throw new ValueRuntimeException(MessageCode.CERT_KEY_ERR_DEL);
                }
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
        }
    }

    private String getNewFileName(AppCert appCert, String fileKey, String suffix) {
        String fileName = appCert.getAppId() + "#" + appCert.getCertId() + "#" + fileKey + suffix;
        return fileName;
    }

    @Override
    @Transactional
    public void deleteAppCert(Long certId) {
        AppCert appCert = appCertMapper.selectByPrimaryKey(certId);
        if (appCert == null) {
            throw new ValueRuntimeException(MessageCode.CERT_NULL);
        }
        int count = appCertMapper.deleteByPrimaryKey(certId);
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.CERT_ERR_DELETE);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pushtype", appCert.getType());
        String type = messageCodeUtil.getMessage(CodeUtil.CERT_IOS);
        String upload_url = env.getProperty(CodeUtil.CERT_PROD_UPLOAD_URL);  //地址
        if (type.equals(appCert.getType())) { //苹果证书
            jsonObject.put("bundleid", appCert.getCertId());
            deleteFile(appCert, CodeUtil.CERT_TYPE_FILE);
            deleteFile(appCert, CodeUtil.CERT_TYPE_KEY);
        } else {  //Android证书
            jsonObject.put("apkname", appCert.getApkName());
        }
        if(appCert.getCertEnviron() == 0){
            upload_url = env.getProperty(CodeUtil.CERT_UPLOAD_URL);
        }
        String cert_delete_url = env.getProperty(CodeUtil.CERT_DELETE_URL);
        HttpClientUtil.httpRequest(upload_url + cert_delete_url, CodeUtil.METHOD_POST, CodeUtil.CONTEXT_JSON, jsonObject.toString());
    }
}