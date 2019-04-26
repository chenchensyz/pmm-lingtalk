package cn.com.cybertech.controller;

import cn.com.cybertech.model.WebCompany;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebCompanyService;
import cn.com.cybertech.service.WebUserService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUserController.class);


    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private WebUserService webUserService;

    @Autowired
    private WebCompanyService webCompanyService;

    @RequestMapping(method = RequestMethod.POST)
    public RestResponse login(WebUser webUser, HttpServletRequest request) {
        LOGGER.debug("登录:{}");
        if (StringUtils.isBlank(webUser.getPhone()) || StringUtils.isBlank(webUser.getPassword())
                || webUser.getCompanyId() == null) {
            return RestResponse.failure("请填写完整登录信息");
        }
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String platform = request.getHeader("platform");
        Map<String, Object> map = Maps.newHashMap();
        try {
            map = webUserService.login(webUser, platform);
        } catch (ValueRuntimeException e) {
            msgCode = (int) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(map);
    }

    //获取用户所在的公司列表
    @RequestMapping(value = "/companys/{phone}", method = RequestMethod.GET)
    public RestResponse getCompanyList(@PathVariable("phone") String phone) {
        List<WebCompany> companyInfos = webCompanyService.selectWebCompanyByPhone(phone);
        return RestResponse.success().setData(companyInfos);
    }

}
