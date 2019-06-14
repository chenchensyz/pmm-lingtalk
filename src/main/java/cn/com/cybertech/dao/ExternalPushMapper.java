package cn.com.cybertech.dao;

import cn.com.cybertech.model.ExternalPush;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExternalPushMapper {

    ExternalPush getExternalPushByUuid(String uuid);

    int insertSelective(ExternalPush externalpush);

    int updateByPrimaryKeySelective(ExternalPush externalpush);

    List<ExternalPush> getList(ExternalPush externalpush);

    int deleteByPrimaryKey(Integer id);

    List<ExternalPush> getByuuids(List<String> ids);

}
