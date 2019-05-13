package cn.com.cybertech.dao;

import cn.com.cybertech.model.AppInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppInfoMapper {

    AppInfo getAppInfoById(Integer id);

    AppInfo queryAppByAppId(String appId);

    int insertAppInfo(AppInfo appInfo);

    int updateAppInfo(AppInfo appInfo);

    List<AppInfo> getAppInfoList(AppInfo appInfo);

    String queryUserAppInfo(@Param("userId") Long userId, @Param("companyId") Integer companyId);
}