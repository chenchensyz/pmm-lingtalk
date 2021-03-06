package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.ExternalTo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExternalToMapper {

    List<ExternalTo> getByUuidAndTo(@Param("uuid") String uuid, @Param("to") String to);

    int insertExternalTo(ExternalTo externalTo);

    int updateExternalTo(ExternalTo externalTo);

    List<ExternalTo> getPushUserDetail(@Param("to") String to, @Param("appId") String appId);

    //批量插入
    int addExternalToMore(@Param("uuid") String uuid, @Param("tos") List<String> tos,
                          @Param("expire") Long expire, @Param("offLine") Integer offLine);

    List<ExternalTo> getByUuid(String uuid);
}