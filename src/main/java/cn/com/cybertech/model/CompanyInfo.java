package cn.com.cybertech.model;

import cn.com.cybertech.model.common.BaseEntity;

import java.util.Date;

public class CompanyInfo extends BaseEntity {
    private Integer companyId;

    private String companyName;

    private String companyLicence;

    private String companyLicenceUrl;

    private Integer companyStatus;

    private Date companyStatusTime;

    private Date companyApplyTime;

    private Integer companyDeleted;

    private Date companyDeletedTime;

    private String companyRemark;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName == null ? null : companyName.trim();
    }

    public String getCompanyLicence() {
        return companyLicence;
    }

    public void setCompanyLicence(String companyLicence) {
        this.companyLicence = companyLicence == null ? null : companyLicence.trim();
    }

    public String getCompanyLicenceUrl() {
        return companyLicenceUrl;
    }

    public void setCompanyLicenceUrl(String companyLicenceUrl) {
        this.companyLicenceUrl = companyLicenceUrl == null ? null : companyLicenceUrl.trim();
    }

    public Integer getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(Integer companyStatus) {
        this.companyStatus = companyStatus;
    }

    public Date getCompanyStatusTime() {
        return companyStatusTime;
    }

    public void setCompanyStatusTime(Date companyStatusTime) {
        this.companyStatusTime = companyStatusTime;
    }

    public Date getCompanyApplyTime() {
        return companyApplyTime;
    }

    public void setCompanyApplyTime(Date companyApplyTime) {
        this.companyApplyTime = companyApplyTime;
    }

    public Integer getCompanyDeleted() {
        return companyDeleted;
    }

    public void setCompanyDeleted(Integer companyDeleted) {
        this.companyDeleted = companyDeleted;
    }

    public Date getCompanyDeletedTime() {
        return companyDeletedTime;
    }

    public void setCompanyDeletedTime(Date companyDeletedTime) {
        this.companyDeletedTime = companyDeletedTime;
    }

    public String getCompanyRemark() {
        return companyRemark;
    }

    public void setCompanyRemark(String companyRemark) {
        this.companyRemark = companyRemark == null ? null : companyRemark.trim();
    }
}