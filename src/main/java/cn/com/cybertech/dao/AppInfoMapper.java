package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.AppInfo;

public interface AppInfoMapper extends BaseDao<AppInfo> {

    AppInfo queryAppByAppId(String appId);
}