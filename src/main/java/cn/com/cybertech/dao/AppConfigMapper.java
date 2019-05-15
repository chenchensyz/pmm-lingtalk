package cn.com.cybertech.dao;

import cn.com.cybertech.model.AppConfig;

import java.util.List;

public interface AppConfigMapper {

    List<AppConfig> getAppConfigListByAppId(int appId);
}