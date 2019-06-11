package cn.com.cybertech.controller;


import cn.com.cybertech.model.AppCert;
import cn.com.cybertech.service.AppCertService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/app/cert")
public class AppCertController {


    @Autowired
    private AppCertService appCertService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestResponse queryAppCertList(AppCert appCert) {
        PageHelper.startPage(appCert.getPageNum(), appCert.getPageSize());
        List<AppCert> appCertList = appCertService.getAppCertList(appCert);
        PageInfo<AppCert> appCertPage = new PageInfo<AppCert>(appCertList);
        return RestResponse.success().setData(appCertList)
                .setTotal(appCertPage.getTotal()).setPage(appCertPage.getLastPage());
    }

    @RequestMapping(value = "/addOrEditAppCert")
    public RestResponse addOrEditAppCert(HttpServletRequest request, AppCert appCert) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appCertService.addOrEditAppCert(request, appCert);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping(value = "/delAppCert")
    public RestResponse delAppCert(Long certId) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appCertService.deleteAppCert(certId);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

}
