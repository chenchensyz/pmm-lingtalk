package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.CommonDicts;
import org.apache.ibatis.annotations.Param;

public interface CommonDictsMapper extends BaseDao<CommonDicts> {

    CommonDicts findPlatform(@Param("dictType") Integer dictType, @Param("dictCode") String dictCode);
}