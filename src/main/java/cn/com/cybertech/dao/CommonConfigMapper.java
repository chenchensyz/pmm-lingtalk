package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.CommonConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommonConfigMapper extends BaseDao<CommonConfig> {

    List<CommonConfig> findCommonConfig(@Param("platform") String platform, @Param("version") String version);

    String queryMaxVesrion(String platform);
}