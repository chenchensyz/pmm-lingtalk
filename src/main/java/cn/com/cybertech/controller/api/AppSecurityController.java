package cn.com.cybertech.controller.api;

import cn.com.cybertech.service.AppSecurityService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/api/security")
public class AppSecurityController {

    @Autowired
    private AppSecurityService appSecurityService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    /**
     * 获取公众号的全局唯一票据
     */
//    @RequestMapping(value = "/token", method = RequestMethod.POST)
//    public @ResponseBody
//    Object queryToken(HttpServletRequest request, String appId, String secret) {
//        try {
//            return getSuccessResult().addAttribute(AppConstants.RET_MESSAGE_DATAS, securityServiceImpl.queryToken(appId, secret));
//        } catch (ValueRuntimeException e) {
//            return new Result((Integer) e.getValue(), MessageUtils.getMessage(request, String.valueOf(e.getValue())));
//        }
//    }
    @RequestMapping(value = "/userlogin")
    public RestResponse userlogin(String appId, String userId, String password, String platform) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            response = appSecurityService.userlogin(response, appId, userId, password, platform);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.retMsg(msgCode, messageCodeUtil.getMessage(msgCode)).setData(response);
    }
}
