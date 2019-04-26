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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appInfo")
public class AppInfoController {

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping("/list")
    public RestResponse queryAppInfoList(AppInfo appInfo) {
        PageHelper.startPage(appInfo.getPageNum(), appInfo.getPageSize());
        List<AppInfo> appInfos = appInfoService.queryAppList(appInfo);
        PageInfo<AppInfo> appInfoPage = new PageInfo<AppInfo>(appInfos);
        return RestResponse.success().setData(appInfos)
                .setTotal(appInfoPage.getTotal()).setPage(appInfoPage.getLastPage());
    }

    @RequestMapping("addOrEdid")
    public RestResponse addOrEdidAppInfo(HttpServletRequest request, AppInfo appInfo) {
        String token = request.getHeader("token");
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appInfoService.insertAppInfo(token, appInfo);
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
