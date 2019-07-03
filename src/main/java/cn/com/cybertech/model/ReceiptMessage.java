package cn.com.cybertech.model;

import java.io.Serializable;
import java.util.List;

public class ReceiptMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String messageId;
    private List<ReceiptMessageStatus> statusSet;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public List<ReceiptMessageStatus> getStatusSet() {
        return statusSet;
    }

    public void setStatusSet(List<ReceiptMessageStatus> statusSet) {
        this.statusSet = statusSet;
    }

}
