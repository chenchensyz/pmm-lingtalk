package cn.com.cybertech.controller;

import cn.com.cybertech.model.WebPermission;
import cn.com.cybertech.model.WebRole;
import cn.com.cybertech.service.WebPerimissionService;
import cn.com.cybertech.tools.EncryptUtils;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/web/permission")
public class WebPerimissionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebPerimissionController.class);

    @Autowired
    private WebPerimissionService webPerimissionService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    //查询用户权限列表
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public RestResponse getPermissionList(Integer roleId, Integer type) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        List<WebPermission> permissions = webPerimissionService.getPermissions(roleId, Arrays.asList(type));
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(permissions);
    }

    //查找用户的菜单权限标识集合
    @RequestMapping(value = "/list/{userName}", method = RequestMethod.GET)
    public RestResponse findPermissions(@PathVariable("userName") String userName) {
        Set<String> permissions = webPerimissionService.findPermissions(userName);
        return RestResponse.success().setData(permissions);
    }

    @RequestMapping("/addOrEditPerm")
    public RestResponse addOrEditPerm(WebPermission webPermission) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            webPerimissionService.addOrEditPerm(webPermission);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping("/deletePerm")
    public RestResponse deletePerm(String ids) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        System.out.println(ids);
        try {
            webPerimissionService.deletePerm(ids);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }


    public static void main(String[] args) {
        System.out.println(EncryptUtils.SHA256Encode("cyber"));
        System.out.println(EncryptUtils.MD5Encode("cyber"));
    }
}
