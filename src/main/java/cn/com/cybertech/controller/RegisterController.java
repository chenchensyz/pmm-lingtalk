package cn.com.cybertech.controller;


import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebUserService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private WebUserService webUserService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping()
    public RestResponse register(WebUser webUser, String companyName, String introduction) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            webUserService.registerUser(webUser, companyName, introduction);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping(value = "/findPasswordFlag/{phone}", method = RequestMethod.GET)
    public RestResponse findPasswordFlag(@PathVariable("phone") String phone) {
        boolean flag = false;
        WebUser user = webUserService.getWebUserByPhone(phone, null);
        if (user != null && StringUtils.isNotBlank(user.getPassword())) {
            flag = true;
        }
        return RestResponse.success().setData(flag);
    }

}
