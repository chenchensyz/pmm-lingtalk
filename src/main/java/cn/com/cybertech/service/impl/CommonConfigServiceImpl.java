package cn.com.cybertech.service.impl;

import cn.com.cybertech.config.redis.RedisTool;
import cn.com.cybertech.dao.CommonConfigMapper;
import cn.com.cybertech.model.CommonConfig;
import cn.com.cybertech.model.SysUser;
import cn.com.cybertech.service.CommonConfigService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("commonConfigService")
public class CommonConfigServiceImpl implements CommonConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonConfigServiceImpl.class);

    @Autowired
    private CommonConfigMapper commonConfigMapper;

    @Autowired
    private RedisTool redisTool;

    @Override
    public List<CommonConfig> getCommonConfigList(CommonConfig commonConfig) {
        return commonConfigMapper.getCommonConfigList(commonConfig);
    }

    @Override
    public void addOrEditCommonConfig(String token, CommonConfig commonConfig) {
        SysUser localUser = redisTool.getSysUser(CodeUtil.REDIS_PREFIX + token);
        int count;
        if (commonConfig.getId() == null) {  //新增
            commonConfig.setCreateUser(localUser.getUserName());
            count = commonConfigMapper.insertCommonConfig(commonConfig);
        } else {
            commonConfig.setUpdateUser(localUser.getUserName());
            count = commonConfigMapper.updateCommonConfig(commonConfig);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.COMMONCONFIG_ERR_SAVE);
        }
    }

    @Override
    @Transactional
    public void deleteCommonConfig(Integer id) {
        int count = commonConfigMapper.deleteCommonConfigById(id);
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.COMMONCONFIG_ERR_DEL);
        }
    }

    @Override
    public List<String> getVersionByPlatform(String platform) {
        return commonConfigMapper.getVersionByPlatform(platform);
    }

    @Override
    @Transactional
    public void copyCommonConfig(String token, String platform, String fromVersion, String toVersion) {
        List<CommonConfig> fromConfig = commonConfigMapper.findCommonConfig(platform, fromVersion);
        if (fromConfig == null || fromConfig.isEmpty()) {
            throw new ValueRuntimeException(MessageCode.COMMONCONFIG_FROM_NULL);  //源版本配置不存在
        }
        List<CommonConfig> toConfig = commonConfigMapper.findCommonConfig(platform, toVersion);
        if (toConfig == null || toConfig.isEmpty()) {
            throw new ValueRuntimeException(MessageCode.COMMONCONFIG_TO_NULL);  //目标版本不存在
        }
        commonConfigMapper.deleteConfigByPlatformAndVersion(platform, toVersion); //删除目标原配置
        SysUser localUser = redisTool.getSysUser(CodeUtil.REDIS_PREFIX + token);
        for (CommonConfig com : fromConfig) {
            com.setCreateUser(localUser.getUserName());
            com.setVersion(toConfig.get(0).getVersion());
        }
        int count = commonConfigMapper.insertMoreCommonConfig(fromConfig);
        if (count != fromConfig.size()) {
            throw new ValueRuntimeException(MessageCode.COMMONCONFIG_ERR_SAVE);  //全局配置保存失败
        }
    }
}