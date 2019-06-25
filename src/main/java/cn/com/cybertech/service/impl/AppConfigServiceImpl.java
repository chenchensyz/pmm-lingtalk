package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppConfigMapper;
import cn.com.cybertech.model.AppConfig;
import cn.com.cybertech.service.AppConfigService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("appConfigService")
public class AppConfigServiceImpl implements AppConfigService {

    @Autowired
    private AppConfigMapper appConfigMapper;

    @Override
    public List<AppConfig> queryAppConfigList(AppConfig appConfig) {
        List<AppConfig> list = appConfigMapper.getAppConfigListByAppId(appConfig.getAppId());
        return list;
    }

    @Override
    @Transactional
    public void changeAppConfig(AppConfig appConfig) {
        appConfigMapper.deleteByAppId(appConfig.getAppId());
        if (appConfig.getAppConfigs() != null && appConfig.getAppConfigs().size() > 0) {
            int count = appConfigMapper.insertMoreAppConfig(appConfig);
            if (count != appConfig.getAppConfigs().size()) {
                throw new ValueRuntimeException(MessageCode.CONFIG_ERR_ADD);
            }
        }
    }
}
