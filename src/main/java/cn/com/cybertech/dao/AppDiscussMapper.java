package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.AppDiscuss;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppDiscussMapper extends BaseDao<AppDiscuss> {

    AppDiscuss getAppDiscussById(Integer discussId);

    int insertAppDiscuss(AppDiscuss appDiscuss);

    int updateAppDiscuss(AppDiscuss appDiscuss);

    //分页查询
    int getAppDiscussCount(AppDiscuss appDiscuss);

    List<Integer> getAppDiscussIdList(AppDiscuss appDiscuss);

    List<AppDiscuss> getAppDiscussList(List<Integer> discussIds);

    //根据创建者批量删除讨论组
    int deleteDiscussInCreatorIds(List<String> creatorId);

    //统计app下所有讨论组
    int countAppDiscussIdsByAppId(Integer appId);

    //删除app下所有讨论组
    int deleteAppDiscussIdsByAppId(Integer appId);

    //批量修改为已删除
    int updateAppDiscussDisabled(List<Integer> discussIds);


    List<AppDiscuss> getAppApiDiscussList(@Param("discussIdList") List<Integer> discussIdList,
                                          @Param("userId") String userId, @Param("appId") Integer appId);

    //批量增加成员
    int addDiscussInUserIds(AppDiscuss appDiscuss);

    //批量删除成员
    int delDiscussInUserIds(AppDiscuss appDiscuss);
}