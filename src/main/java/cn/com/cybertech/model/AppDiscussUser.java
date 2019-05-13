package cn.com.cybertech.model;

import cn.com.cybertech.model.common.BaseEntity;
import java.util.Date;

public class AppDiscussUser extends BaseEntity {
    private Integer id;

    private Integer discussId;

    private String userId;

    private String added;

    private Date addTime;

    private String cardname;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDiscussId() {
        return discussId;
    }

    public void setDiscussId(Integer discussId) {
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

    public String getCardname() {
        return cardname;
    }

    public void setCardname(String cardname) {
        this.cardname = cardname == null ? null : cardname.trim();
    }
}