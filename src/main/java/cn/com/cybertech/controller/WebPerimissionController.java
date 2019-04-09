package cn.com.cybertech.controller;

import cn.com.cybertech.model.WebPermission;
import cn.com.cybertech.service.WebPerimissionService;
import cn.com.cybertech.tools.EncryptUtils;
import cn.com.cybertech.tools.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/web/perimission")
public class WebPerimissionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebPerimissionController.class);

    @Autowired
    private WebPerimissionService webPerimissionService;

    //查询用户权限列表
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public RestResponse getPerimissionList(@RequestParam String userName) {
        List<WebPermission> permissions = webPerimissionService.getPermissions(userName, 3);
        return RestResponse.success().setData(permissions);
    }

    //查找用户的菜单权限标识集合
    @RequestMapping(value = "/list/{userName}", method = RequestMethod.GET)
    public RestResponse findPermissions(@PathVariable("userName") String userName) {
        Set<String> permissions = webPerimissionService.findPermissions(userName);
        return RestResponse.success().setData(permissions);
    }


    public static void main(String[] args) {
        System.out.println(EncryptUtils.SHA256Encode("cyber"));
        System.out.println(EncryptUtils.MD5Encode("cyber"));
    }
}
