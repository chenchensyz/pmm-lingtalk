package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.CommonConfig;

import java.util.List;

public interface CommonConfigMapper extends BaseDao<CommonConfig> {

    List<CommonConfig> findCommonConfig(String platform, String version);

    String queryMaxVesrion(String platform);
}