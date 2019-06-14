package cn.com.cybertech.controller.api;

import cn.com.cybertech.service.AppSecurityService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/api/security")
public class AppSecurityController {

    @Autowired
    private AppSecurityService appSecurityService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    /**
     * sdk获取token
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public RestResponse queryToken(@RequestParam String appId, @RequestParam String secret) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            response = appSecurityService.queryToken(response, appId, secret);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

    @RequestMapping(value = "/userlogin")
    public RestResponse userlogin(@RequestParam String appId, @RequestParam String userId,
                                  @RequestParam String password, @RequestParam String platform) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            response = appSecurityService.userlogin(response, appId, userId, password, platform);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }
}
