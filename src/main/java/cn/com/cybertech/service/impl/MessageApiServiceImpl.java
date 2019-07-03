package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.MessageApiMapper;
import cn.com.cybertech.model.ReceiptMessage;
import cn.com.cybertech.service.MessageApiService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service("messageApiService")
public class MessageApiServiceImpl implements MessageApiService {

    @Autowired
    private MessageApiMapper messageApiMapper;

    @Override
    public RestResponse getReceiptMessageStatus(RestResponse response, String messageIds) {
        List<String> msgIds = Arrays.asList(messageIds.split(";"));
        if (msgIds == null || msgIds.isEmpty()) {
            throw new ValueRuntimeException(MessageCode.MESSAGE_SELECT_NULL);
        }
        List<ReceiptMessage> receiptMessages = messageApiMapper.queryReceiptMessageStatus(msgIds);
        response.retDatas(receiptMessages);
        return response;
    }
}
