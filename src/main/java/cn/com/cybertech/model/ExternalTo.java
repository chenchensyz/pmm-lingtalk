package cn.com.cybertech.model;

import java.util.Date;

public class ExternalTo {
    private String to;

    private Integer state;

    private Date pushtime;

    private Date acktime;

    private String uuid;

    private Long expire;

    private Boolean offLine = false;

    private ExternalPush push;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getPushtime() {
        return pushtime;
    }

    public void setPushtime(Date pushtime) {
        this.pushtime = pushtime;
    }

    public Date getAcktime() {
        return acktime;
    }

    public void setAcktime(Date acktime) {
        this.acktime = acktime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getExpire() {
        return expire;
    }

    public void setExpire(Long expire) {
        this.expire = expire;
    }

    public Boolean getOffLine() {
        return offLine;
    }

    public void setOffLine(Boolean offLine) {
        this.offLine = offLine;
    }

    public ExternalPush getPush() {
        return push;
    }

    public void setPush(ExternalPush push) {
        this.push = push;
    }
}