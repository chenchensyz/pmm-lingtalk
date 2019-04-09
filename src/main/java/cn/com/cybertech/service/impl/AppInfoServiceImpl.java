package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppInfoMapper;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.service.AppInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("appInfoService")
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Override
    public List<AppInfo> queryApp(AppInfo appInfo) {
        return appInfoMapper.getList(appInfo);
    }
}
