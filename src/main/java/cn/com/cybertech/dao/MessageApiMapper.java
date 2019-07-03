package cn.com.cybertech.dao;

import cn.com.cybertech.model.ReceiptMessage;

import java.util.List;

/**
 * 回执消息
 */
public interface MessageApiMapper {

    List<ReceiptMessage> queryReceiptMessageStatus(List<String> msgIds);
}
