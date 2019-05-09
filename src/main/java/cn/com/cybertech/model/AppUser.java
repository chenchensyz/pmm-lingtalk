package cn.com.cybertech.model;

import cn.com.cybertech.model.common.BaseEntity;
import java.util.Date;

public class AppUser extends BaseEntity {
    private String id;

    private String userId;

    private String userName;

    private Integer appId;

    private Integer loginInfoId;

    private String userPasswd;

    private Date createTime;

    private String roleId;

    private Integer disabled;

    private Integer passwdStatus;

    private Integer companyId;

    private String userFullid;

    private Integer isAdmin;

    private Integer userState;

    private Integer isFrozen;

    private String authorityDepartment;

    private Integer userRoleId;

    private Integer isCs;

    private String modules;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public Integer getLoginInfoId() {
        return loginInfoId;
    }

    public void setLoginInfoId(Integer loginInfoId) {
        this.loginInfoId = loginInfoId;
    }

    public String getUserPasswd() {
        return userPasswd;
    }

    public void setUserPasswd(String userPasswd) {
        this.userPasswd = userPasswd == null ? null : userPasswd.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId == null ? null : roleId.trim();
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public Integer getPasswdStatus() {
        return passwdStatus;
    }

    public void setPasswdStatus(Integer passwdStatus) {
        this.passwdStatus = passwdStatus;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getUserFullid() {
        return userFullid;
    }

    public void setUserFullid(String userFullid) {
        this.userFullid = userFullid == null ? null : userFullid.trim();
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Integer getUserState() {
        return userState;
    }

    public void setUserState(Integer userState) {
        this.userState = userState;
    }

    public Integer getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(Integer isFrozen) {
        this.isFrozen = isFrozen;
    }

    public String getAuthorityDepartment() {
        return authorityDepartment;
    }

    public void setAuthorityDepartment(String authorityDepartment) {
        this.authorityDepartment = authorityDepartment == null ? null : authorityDepartment.trim();
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public Integer getIsCs() {
        return isCs;
    }

    public void setIsCs(Integer isCs) {
        this.isCs = isCs;
    }

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules == null ? null : modules.trim();
    }
}