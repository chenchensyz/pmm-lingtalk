package cn.com.cybertech.controller;

import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.service.AppInfoService;
import cn.com.cybertech.tools.EncryptUtils;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/info")
public class AppInfoController {

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("/list")
    public RestResponse queryAppInfoList(HttpServletRequest request, AppInfo appInfo) {
        String token = request.getHeader("token");
        PageHelper.startPage(appInfo.getPageNum(), appInfo.getPageSize());
        List<AppInfo> appInfos = appInfoService.queryAppList(token, appInfo);
        PageInfo<AppInfo> appInfoPage = new PageInfo<AppInfo>(appInfos);
        return RestResponse.success().setData(appInfos)
                .setTotal(appInfoPage.getTotal()).setPage(appInfoPage.getLastPage());
    }

    @RequestMapping(value = "addOrEditAppInfo", method = RequestMethod.POST)
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
    @RequestMapping("detail")
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

    public static void main(String[] args) {
        System.out.println(EncryptUtils.SHA256Encode("cyber"));
        System.out.println(EncryptUtils.MD5Encode("cyber"));
    }
}
