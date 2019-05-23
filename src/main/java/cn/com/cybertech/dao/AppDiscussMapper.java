package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.AppDiscuss;

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

    //获取app下所有讨论组id
    int getAppDiscussIdsByAppId(Integer appId);

    //删除app下所有讨论组
    int deleteAppDiscussIdsByAppId(Integer appId);

    //批量修改为已删除
    int updateAppDiscussDisabled(List<Integer> discussIds);
}