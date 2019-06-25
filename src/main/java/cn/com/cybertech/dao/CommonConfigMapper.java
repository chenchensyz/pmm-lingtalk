package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.CommonConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommonConfigMapper {

    List<CommonConfig> findCommonConfig(@Param("platform") String platform, @Param("version") String version);

    String queryMaxVesrion(String platform);

    List<CommonConfig> getCommonConfigList(CommonConfig commonConfig);

    int insertCommonConfig(CommonConfig commonConfig);

    int updateCommonConfig(CommonConfig commonConfig);

    int deleteCommonConfigById(Integer id);

    List<String> getVersionByPlatform(String platform);

    int deleteConfigByPlatformAndVersion(@Param("platform") String platform, @Param("version") String version);

    int insertMoreCommonConfig(List<CommonConfig> commonConfigs);
}