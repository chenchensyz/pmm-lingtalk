package cn.com.cybertech.controller;

import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.service.AppInfoService;
import cn.com.cybertech.tools.EncryptUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/appInfo")
public class AppInfoController {

    @Autowired
    private AppInfoService appInfoService;

    @RequestMapping
    @ResponseBody
    public Map<String, Object> queryAppInfo() {
        Map<String, Object> map = Maps.newHashMap();
        List<AppInfo> appInfos = appInfoService.queryApp(new AppInfo());
        map.put("appInfos", appInfos);
        return map;
    }

    public static void main(String[] args) {
        System.out.println(EncryptUtils.SHA256Encode("cyber"));
        System.out.println(EncryptUtils.MD5Encode ("cyber"));
    }
}
