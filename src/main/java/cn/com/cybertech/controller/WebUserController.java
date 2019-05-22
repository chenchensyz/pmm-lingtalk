package cn.com.cybertech.controller;

import cn.com.cybertech.config.redis.RedisTool;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.model.WebCompany;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebUserService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/web/user")
public class WebUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebUserController.class);

    @Autowired
    private WebUserService webUserService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;


    @RequestMapping("/list")
    public RestResponse queryWebUserList(HttpServletRequest request, WebUser webUser) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            PageHelper.startPage(webUser.getPageNum(), webUser.getPageSize());
            List<WebUser> webUserList = webUserService.getWebUserList(token, webUser);
            PageInfo<WebUser> webInfoPage = new PageInfo<>(webUserList);
            return RestResponse.success().setData(webUserList)
                    .setTotal(webInfoPage.getTotal()).setPage(webInfoPage.getLastPage());
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping("/addOrEditUser")
    public RestResponse addOrEditUser(HttpServletRequest request, WebUser webUser) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            webUserService.addOrEditUser(token, webUser);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    //查询：用户绑定应用
    @RequestMapping("/getUserApp")
    public RestResponse getUserApp(HttpServletRequest request, Long userId) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        List<Integer> userApp = Lists.newArrayList();
        try {
            userApp = webUserService.getUserApp(token, userId);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(userApp);
    }

    //修改用户状态
    @RequestMapping("/optionUser")
    public RestResponse optionUser(HttpServletRequest request,Long userId, Integer state) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String platform = request.getHeader("platform");
        try {
            webUserService.optionUser(platform,userId, state);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    //获取用户详情
    @RequestMapping("/getUserInfo")
    public RestResponse getUserInfo(HttpServletRequest request) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        WebUser userDetail = webUserService.getUserInfo(token);
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(userDetail);
    }

    //修改用户状态
    @RequestMapping("/resetPassword")
    public RestResponse resetPassword(HttpServletRequest request,String oldPassword, String newPassword) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            webUserService.resetPassword(token,oldPassword,newPassword);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }
}
