package cn.com.cybertech.controller.app;

import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.service.AppInfoService;
import cn.com.cybertech.service.WebUserService;
import cn.com.cybertech.tools.EncryptUtils;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/app/info")
public class AppInfoController {

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private WebUserService webUserService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("/list")
    public RestResponse queryAppInfoList(HttpServletRequest request, AppInfo appInfo) {
        String token = request.getHeader("token");
        RestResponse restResponse = appInfoService.queryAppList(token, appInfo);
        return restResponse;
    }

    @RequestMapping(value = "/addOrEditAppInfo", method = RequestMethod.POST)
    public RestResponse addOrEditAppInfo(HttpServletRequest request, AppInfo appInfo) {
        String token = request.getHeader("token");
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appInfoService.addOrEditAppInfo(token, appInfo);
        } catch (ValueRuntimeException e) {
            msgCode = (int) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    //应用基本信息
    @RequestMapping("/detail")
    public RestResponse getAppDetail(Integer appId) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        AppInfo appInfo = appInfoService.queryAppById(appId);
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(appInfo);
    }

    //获取当前公司所有应用
    @RequestMapping("/queryCompanyAppInfoList")
    public RestResponse queryCompanyAppInfoList(HttpServletRequest request) {
        String token = request.getHeader("token");
        List<AppInfo> appInfos = appInfoService.queryCompanyAppInfoList(token);
        return RestResponse.success().setData(appInfos);
    }

    //删除应用
    @RequestMapping("/deleteAppInfo")
    public RestResponse deleteAppInfo(Integer appId) {
        String users = webUserService.getUserAppByAppId(appId);
        if (StringUtils.isNotBlank(users)) {
            return RestResponse.res(MessageCode.APPINFO_ERR_DEL, "用户："+users+"绑定此应用，请解除用户绑定后删除");
        }
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appInfoService.deleteAppInfo(appId);
        } catch (ValueRuntimeException e) {
            msgCode = (int) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    public static void main(String[] args) {
        System.out.println(EncryptUtils.SHA256Encode("cyber"));
        System.out.println(EncryptUtils.MD5Encode("cyber"));
    }
}
