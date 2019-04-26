package cn.com.cybertech.service;

import cn.com.cybertech.model.AppInfo;

import java.util.List;

public interface AppInfoService {

    List<AppInfo> queryAppList(AppInfo appInfo);

    void insertAppInfo(String token,AppInfo appInfo);
}
