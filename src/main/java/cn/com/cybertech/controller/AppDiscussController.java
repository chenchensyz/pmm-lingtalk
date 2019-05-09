package cn.com.cybertech.controller;


import cn.com.cybertech.model.AppDiscuss;
import cn.com.cybertech.service.AppDiscussService;
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
import java.util.Map;

@RestController
@RequestMapping("/app/discuss")
public class AppDiscussController {


    @Autowired
    private AppDiscussService appDiscussService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestResponse queryAppCertList(AppDiscuss appDiscuss) {
        RestResponse appDiscussMap = appDiscussService.getAppDiscussList(appDiscuss);
        return appDiscussMap;
    }

    @RequestMapping(value = "/addOrEditAppDiscuss")
    public RestResponse addOrEditAppDiscuss(AppDiscuss appDiscuss) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appDiscussService.addOrEditAppDiscuss(appDiscuss);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping(value = "/delAppDiscuss")
    public RestResponse delAppDiscuss(Long userId) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
//        int count = appCertService.delectAppCert(certId);
//        if (count == 0) {
//            msgCode = MessageCode.CERT_ERR_DELETE;
//        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping(value = "/addAppDiscussUser")
    public RestResponse addAppDiscussUser(Integer discussId, String userId) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
//            appDiscussService.addOrEditAppDiscuss(appDiscuss);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }
}
