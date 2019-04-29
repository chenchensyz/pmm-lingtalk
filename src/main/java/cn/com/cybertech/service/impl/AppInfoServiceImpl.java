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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("appInfoService")
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Autowired
    private RedisTool redisTool;

    @Override
    public AppInfo queryAppById(Long id) {
        return appInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<AppInfo> queryAppList(AppInfo appInfo) {
        return appInfoMapper.getList(appInfo);
    }

    @Override
    public void insertAppInfo(String token, AppInfo appInfo) {
        WebUser webUser =redisTool.getUser(CodeUtil.REDIS_PREFIX+token);
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
        int count = appInfoMapper.insertSelective(appInfo);
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_OPERATION);
        }
    }
}
