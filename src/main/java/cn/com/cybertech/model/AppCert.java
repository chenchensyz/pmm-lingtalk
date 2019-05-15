package cn.com.cybertech.model;

import cn.com.cybertech.model.common.BaseEntity;
import cn.com.cybertech.tools.DateUtil;

import java.util.Date;

public class AppCert extends BaseEntity {
    private Long id;

    private Integer appId;

    private String type;

    private String certId;

    private String certKey;

    private String certSecret;

    private String certName;

    private String apkName;

    private Integer certEnviron;

    private byte[] certFile;

    private byte[] keyFile;

    private Integer version;

    private Integer state;

    private Date createTime;

    private Date updateTime;

    private String certSuffix; //证书文件后缀类型

    //非数据库字段
    private String appName;

    private String createTimeStr;

    private String updateTimeStr;

    private Boolean certUpload = false;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId == null ? null : certId.trim();
    }

    public String getCertKey() {
        return certKey;
    }

    public void setCertKey(String certKey) {
        this.certKey = certKey == null ? null : certKey.trim();
    }

    public String getCertSecret() {
        return certSecret;
    }

    public void setCertSecret(String certSecret) {
        this.certSecret = certSecret == null ? null : certSecret.trim();
    }

    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName == null ? null : certName.trim();
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName == null ? null : apkName.trim();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public byte[] getCertFile() {
        return certFile;
    }

    public void setCertFile(byte[] certFile) {
        this.certFile = certFile;
    }

    public byte[] getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(byte[] keyFile) {
        this.keyFile = keyFile;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCreateTimeStr() {
        return createTime == null ? "" : DateUtil.format(createTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getUpdateTimeStr() {
        return updateTime == null ? "" : DateUtil.format(updateTime, DateUtil.YMD_DASH_WITH_TIME);
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }

    public Integer getCertEnviron() {
        return certEnviron;
    }

    public void setCertEnviron(Integer certEnviron) {
        this.certEnviron = certEnviron;
    }

    public Boolean getCertUpload() {
        return certUpload;
    }

    public void setCertUpload(Boolean certUpload) {
        this.certUpload = certUpload;
    }

    public String getCertSuffix() {
        return certSuffix;
    }

    public void setCertSuffix(String certSuffix) {
        this.certSuffix = certSuffix;
    }
}