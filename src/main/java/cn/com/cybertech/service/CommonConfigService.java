package cn.com.cybertech.service;

import cn.com.cybertech.model.CommonConfig;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CommonConfigService {

    List<CommonConfig> getCommonConfigList(CommonConfig commonConfig);

    void addOrEditCommonConfig(String token, CommonConfig commonConfig);

    void deleteCommonConfig(Integer id);

    List<String> getVersionByPlatform(String platform);

    void copyCommonConfig(String token,String platform, String fromVersion, String toVersion);

}