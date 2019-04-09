package cn.com.cybertech.model;

import cn.com.cybertech.model.common.BaseEntity;

import java.util.Date;

public class AppDiscuss extends BaseEntity {
    private Long discussId;

    private String discussName;

    private String creatorId;

    private Integer discussVersion;

    private Long appId;

    private Integer state;

    private Date createTime;

    private Date updateTime;

    public Long getDiscussId() {
        return discussId;
    }

    public void setDiscussId(Long discussId) {
        this.discussId = discussId;
    }

    public String getDiscussName() {
        return discussName;
    }

    public void setDiscussName(String discussName) {
        this.discussName = discussName == null ? null : discussName.trim();
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId == null ? null : creatorId.trim();
    }

    public Integer getDiscussVersion() {
        return discussVersion;
    }

    public void setDiscussVersion(Integer discussVersion) {
        this.discussVersion = discussVersion;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
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
}