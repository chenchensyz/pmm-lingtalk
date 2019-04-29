package cn.com.cybertech.controller;


import cn.com.cybertech.model.AppCert;
import cn.com.cybertech.service.AppCertService;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cert")
public class AppCertController {

    @Autowired
    private AppCertService appCertService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("/list")
    public RestResponse queryAppCertList(AppCert appCert) {
        PageHelper.startPage(appCert.getPageNum(), appCert.getPageSize());
        List<AppCert> appCertList = appCertService.getAppCertList(appCert);
        PageInfo<AppCert> appCertPage = new PageInfo<AppCert>(appCertList);
        return RestResponse.success().setData(appCertList)
                .setTotal(appCertPage.getTotal()).setPage(appCertPage.getLastPage());
    }
}
