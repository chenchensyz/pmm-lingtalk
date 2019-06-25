package cn.com.cybertech.controller;

import cn.com.cybertech.model.AppConfig;
import cn.com.cybertech.service.AppConfigService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/app/config")
public class AppConfigController {

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("/list")
    public RestResponse queryAppConfigList(AppConfig appConfig) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        List<AppConfig> appConfigs = appConfigService.queryAppConfigList(appConfig);
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(appConfigs);
    }

    @RequestMapping(value = "/changeAppConfig", method = RequestMethod.POST)
    public RestResponse changeAppConfig(AppConfig appConfig) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appConfigService.changeAppConfig(appConfig);
        } catch (ValueRuntimeException e) {
            msgCode = (int) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }
//
//    //应用基本信息
//    @RequestMapping("/detail")
//    public RestResponse getAppDetail(Integer appId) {
//        int msgCode = MessageCode.BASE_SUCC_CODE;
//        AppInfo appInfo = appInfoService.queryAppById(appId);
//        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(appInfo);
//    }
//
//    //获取当前公司所有应用
//    @RequestMapping("/queryCompanyAppInfoList")
//    public RestResponse queryCompanyAppInfoList(HttpServletRequest request) {
//        String token = request.getHeader("token");
//        List<AppInfo> appInfos = appInfoService.queryCompanyAppInfoList(token);
//        return RestResponse.success().setData(appInfos);
//    }
//
//    //删除应用
//    @RequestMapping("/deleteAppInfo")
//    public RestResponse deleteAppInfo(Integer appId) {
//        String users = webUserService.getUserAppByAppId(appId);
//        if (StringUtils.isNotBlank(users)) {
//            return RestResponse.res(MessageCode.APPINFO_ERR_DEL, "用户："+users+"绑定此应用，请解除用户绑定后删除");
//        }
//        int msgCode = MessageCode.BASE_SUCC_CODE;
//        try {
//            appInfoService.deleteAppInfo(appId);
//        } catch (ValueRuntimeException e) {
//            msgCode = (int) e.getValue();
//        }
//        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
//    }
//
//    public static void main(String[] args) {
//        System.out.println(EncryptUtils.SHA256Encode("cyber"));
//        System.out.println(EncryptUtils.MD5Encode("cyber"));
//    }
}
