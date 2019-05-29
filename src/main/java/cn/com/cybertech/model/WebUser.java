package cn.com.cybertech.model;

import cn.com.cybertech.model.common.BaseEntity;
import cn.com.cybertech.tools.DateUtil;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class WebUser extends BaseEntity {
    private Long id;

    private String userName;

    private String password;

    private String nickName;

    private String email;

    private Integer companyId;

    private Integer roleId;

    private Integer state;

    private String remark;

    private Date createTime;

    private Date updateTime;

    private String createTimeStr;

    private String updateTimeStr;

    //绑定的应用
    private List<Integer> appCheckedList = Lists.newArrayList();

    private String source;   //用户来源  sys后台用户，web公司用户

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getNickName() {
        return nickName == null ? "" : nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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

    public List<Integer> getAppCheckedList() {
        return appCheckedList;
    }

    public void setAppCheckedList(List<Integer> appCheckedList) {
        this.appCheckedList = appCheckedList;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}