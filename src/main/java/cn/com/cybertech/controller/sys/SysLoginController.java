package cn.com.cybertech.controller.sys;

import cn.com.cybertech.model.SysUser;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.SysUserService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/sys/login")
public class SysLoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysLoginController.class);


    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private SysUserService sysUserService;


    @RequestMapping(method = RequestMethod.POST)
    public RestResponse login(SysUser sysUser, HttpServletRequest request) {
        LOGGER.debug("系统后台登录:{}");
        if (StringUtils.isBlank(sysUser.getUserName()) || StringUtils.isBlank(sysUser.getPassword())) {
            return RestResponse.failure("请填写完整登录信息");
        }
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String platform = request.getHeader("platform");
        Map<String, Object> map = Maps.newHashMap();
        try {
            map = sysUserService.login(sysUser, platform);
        } catch (ValueRuntimeException e) {
            msgCode = (int) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(map);
    }
}
