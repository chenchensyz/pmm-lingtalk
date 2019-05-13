package cn.com.cybertech.service.impl;

import cn.com.cybertech.config.redis.RedisTool;
import cn.com.cybertech.dao.AppInfoMapper;
import cn.com.cybertech.dao.WebUserMapper;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.AppInfoService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.RandomUtils;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("appInfoService")
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Autowired
    private WebUserMapper webUserMapper;

    @Autowired
    private RedisTool redisTool;

    @Override
    public AppInfo queryAppById(Integer id) {
        return appInfoMapper.getAppInfoById(id);
    }

    @Override
    public List<AppInfo> queryAppList(String token, AppInfo appInfo) {
        WebUser webUser = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
        appInfo.setCompanyId(webUser.getCompanyId());
        List<Integer> userApp = webUserMapper.getUserApp(webUser.getId(), webUser.getCompanyId());
        if (userApp != null && userApp.size() > 0) { //绑定过应用
            appInfo.setUserId(webUser.getId());
        }
        return appInfoMapper.getAppInfoList(appInfo);
    }

    @Override
    public void addOrEditAppInfo(String token, AppInfo appInfo) {
        int count;
        if (appInfo.getId() == null) { //新增
            WebUser webUser = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
            String appSecret = RandomUtils.generateString(32).toUpperCase();
            String appId;
            AppInfo queryApp;
            do {
                appId = RandomUtils.generateString(8).toUpperCase(); //生成8位appid
                queryApp = appInfoMapper.queryAppByAppId(appId);
            } while (queryApp != null);
            appInfo.setAppId(appId);
            appInfo.setAppSecret(appSecret);
            appInfo.setCompanyId(webUser.getCompanyId());
            appInfo.setState(2);
            appInfo.setType(1);
            appInfo.setCreateTime(new Date());
            count = appInfoMapper.insertAppInfo(appInfo);
        } else {
            appInfo.setUpdateTime(new Date());
            count = appInfoMapper.updateAppInfo(appInfo);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_OPERATION);
        }
    }

    @Override
    public List<AppInfo> queryCompanyAppInfoList(String token) {
        WebUser webUser = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
        AppInfo appInfo = new AppInfo();
        appInfo.setCompanyId(webUser.getCompanyId());
        List<AppInfo> appInfoList = appInfoMapper.getAppInfoList(appInfo);
        return appInfoList;
    }

}
