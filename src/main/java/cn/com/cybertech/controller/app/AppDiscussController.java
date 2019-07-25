package cn.com.cybertech.controller.app;


import cn.com.cybertech.model.AppDiscuss;
import cn.com.cybertech.service.AppDiscussService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
            appDiscussService.addOrEditAppDiscuss(null, appDiscuss);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    //删除讨论组
    @RequestMapping(value = "/delAppDiscuss")
    public RestResponse delAppDiscuss(String checkedIds) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appDiscussService.deleteAppDiscuss(checkedIds);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    //添加讨论组成员
    @RequestMapping(value = "/addAppDiscussUser")
    public RestResponse addAppDiscussUser(Integer appId, Integer discussId, String userId) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appDiscussService.addAppDiscussUser(appId, discussId, userId);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    //删除讨论组成员
    @RequestMapping(value = "/delAppDiscussUser")
    public RestResponse delAppDiscussUser(Integer appId, Integer discussId, String userId) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appDiscussService.delAppDiscussUser(appId, discussId, userId);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }
}
