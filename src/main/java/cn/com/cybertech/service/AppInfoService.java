package cn.com.cybertech.service;

import cn.com.cybertech.model.AppInfo;

import java.util.List;

public interface AppInfoService {

    AppInfo queryAppById(Integer id);

    List<AppInfo> queryAppList(String token, AppInfo appInfo);

    void addOrEditAppInfo(String token, AppInfo appInfo);

    List<AppInfo> queryCompanyAppInfoList(String token);


}
