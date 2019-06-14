package cn.com.cybertech.controller.api;

import cn.com.cybertech.service.AppUserService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("app/api/userInfo")
public class AppUserApiController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    /**
     * 增加用户
     */
    @RequestMapping(value = "/add")
    public RestResponse addOrEditAppUser(HttpServletRequest request, @RequestBody Map<String, Object> paramMap) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        RestResponse response = new RestResponse();
        try {
            String token = request.getHeader("token");
            response = appUserService.addAppApiUser(response, token, paramMap);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }


    //删除用户
    @RequestMapping(value = "/delete")
    public RestResponse deleteUser(HttpServletRequest request, @RequestBody Map<String, Object> paramMap) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            String token = request.getHeader("token");
            appUserService.deleteAppApiUser(token, paramMap);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

    //查找用户
    @RequestMapping(value = "/query")
    public @ResponseBody
    RestResponse queryUser(HttpServletRequest request, @RequestBody Map<String, Object> paramMap) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            String token = request.getHeader("token");
            response = appUserService.queryAppApiUser(response, token, paramMap);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

}
