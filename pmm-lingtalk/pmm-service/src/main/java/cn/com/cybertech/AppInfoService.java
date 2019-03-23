package cn.com.cybertech;

import cn.com.cybertech.model.AppInfo;

import java.util.List;

public interface AppInfoService {

    List<AppInfo> queryApp(AppInfo appInfo);
}
