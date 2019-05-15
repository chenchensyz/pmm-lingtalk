package cn.com.cybertech.service;

import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.tools.RestResponse;

import java.util.List;

public interface AppInfoService {

    AppInfo queryAppById(Integer id);

    RestResponse queryAppList(String token, AppInfo appInfo);

    void addOrEditAppInfo(String token, AppInfo appInfo);

    List<AppInfo> queryCompanyAppInfoList(String token);

    void deleteAppInfo(Integer appId);

}
