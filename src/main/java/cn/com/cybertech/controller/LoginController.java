package cn.com.cybertech.controller;

import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebUserService;
import cn.com.cybertech.tools.*;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUserController.class);

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private WebUserService webUserService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping()
    public RestResponse login(@RequestBody WebUser webUser, HttpServletRequest request) {
        LOGGER.debug("登录:{}");
        if (StringUtils.isBlank(webUser.getPhone()) || StringUtils.isBlank(webUser.getPassword())
                || webUser.getCompanyId() == null) {
            return RestResponse.failure("请填写完整登录信息");
        }
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String platform = request.getHeader("platform");

        Map<String, Object> map = Maps.newHashMap();
        try {
            //密码加密规则
            map = webUserService.login(webUser, platform);
        } catch (ValueRuntimeException e) {
            msgCode = (int) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(map);
    }

}
