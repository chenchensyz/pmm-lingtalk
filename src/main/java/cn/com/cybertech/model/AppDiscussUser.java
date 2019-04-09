package cn.com.cybertech.model;

import cn.com.cybertech.model.common.BaseEntity;

import java.util.Date;

public class AppDiscussUser extends BaseEntity {
    private Long id;

    private Long discussId;

    private String userId;

    private String added;

    private Date addTime;

    private String cardName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDiscussId() {
        return discussId;
    }

    public void setDiscussId(Long discussId) {
        this.discussId = discussId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added == null ? null : added.trim();
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName == null ? null : cardName.trim();
    }
}