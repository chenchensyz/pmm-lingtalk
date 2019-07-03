package cn.com.cybertech.model;

import java.io.Serializable;

public class ReceiptMessageStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String userId;
    private Integer status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
