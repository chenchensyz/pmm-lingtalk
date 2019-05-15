package cn.com.cybertech.controller.api;


import cn.com.cybertech.service.AppCertService;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/api/cert")
public class AppCertApiController {

    @Autowired
    private AppCertService appCertService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("/list")
    public List<Map<String, Object>> queryAppCertList() {
        List<Map<String, Object>> apiAppCertList = appCertService.getApiAppCertList();
        return apiAppCertList;
    }
}
