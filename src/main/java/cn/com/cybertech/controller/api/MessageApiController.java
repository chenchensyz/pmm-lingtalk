package cn.com.cybertech.controller.api;

import cn.com.cybertech.service.MessageApiService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回执消息
 */
@RestController
@RequestMapping("/app/api/message")
public class MessageApiController {

    @Autowired
    private MessageApiService messageApiService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping(value = "/receipt/status", method = RequestMethod.GET)
    public RestResponse getReceiptMessageStatus(@RequestParam String messageIds) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            response = messageApiService.getReceiptMessageStatus(response, messageIds);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }
}
