package cn.com.cybertech.dao;

import cn.com.cybertech.model.AppInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppInfoMapper {

    AppInfo getAppInfoById(Integer id);

    AppInfo queryAppByAppId(String appId);

    int insertAppInfo(AppInfo appInfo);

    int updateAppInfo(AppInfo appInfo);

    int getAppInfoCount(AppInfo appInfo);

    List<AppInfo> getAppInfoList(AppInfo appInfo);

    int countUserAppInfo(@Param("userId") Long userId, @Param("companyId") Integer companyId);

    int updateAppInfoState(AppInfo appInfo);
}