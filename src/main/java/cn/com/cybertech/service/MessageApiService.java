package cn.com.cybertech.service;

import cn.com.cybertech.tools.RestResponse;

/**
 * 回执消息
 */
public interface MessageApiService {

    RestResponse getReceiptMessageStatus(RestResponse response, String messageIds);

}