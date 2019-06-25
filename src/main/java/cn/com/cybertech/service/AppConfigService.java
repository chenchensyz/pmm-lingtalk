package cn.com.cybertech.service;

import cn.com.cybertech.model.AppConfig;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.tools.RestResponse;

import java.util.List;

public interface AppConfigService {


    List<AppConfig> queryAppConfigList(AppConfig appConfig);

    void changeAppConfig(AppConfig appConfig);
}
