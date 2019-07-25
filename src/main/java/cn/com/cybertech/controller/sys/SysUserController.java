package cn.com.cybertech.controller.sys;

import cn.com.cybertech.model.SysUser;
import cn.com.cybertech.service.SysUserService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/sys/user")
public class SysUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SysUserController.class);

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;


    @RequestMapping("/list")
    public RestResponse queryWebUserList(HttpServletRequest request, SysUser sysUser) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        RestResponse rest = new RestResponse();
        try {
            PageHelper.startPage(sysUser.getPageNum(), sysUser.getPageSize());
            List<SysUser> sysUsers = sysUserService.getSysUserList(token, sysUser);
            PageInfo<SysUser> sysUsersPage = new PageInfo<>(sysUsers);
            rest.setData(sysUsers).setTotal(sysUsersPage.getTotal()).setPage(sysUsersPage.getLastPage());
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        rest.setCode(msgCode);
        rest.setMessage(messageCodeUtil.getMessage(msgCode));
        return rest;
    }

    @RequestMapping("/addOrEditUser")
    public RestResponse addOrEditUser(HttpServletRequest request, SysUser sysUser) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        String platform = request.getHeader("platform");
        try {
            sysUserService.addOrEditUser(token, platform, sysUser);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    //修改用户状态
    @RequestMapping("/optionUser")
    public RestResponse optionUser(HttpServletRequest request, Long userId, Integer state) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String platform = request.getHeader("platform");
        try {
            sysUserService.optionUser(platform, userId, state);
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
        SysUser userDetail = sysUserService.getUserInfo(token);
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(userDetail);
    }

    //修改密码
    @RequestMapping("/resetPassword")
    public RestResponse resetPassword(HttpServletRequest request, String oldPassword, String newPassword) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            sysUserService.resetPassword(token, oldPassword, newPassword);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }
}
