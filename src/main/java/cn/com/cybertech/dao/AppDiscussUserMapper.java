package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.AppDiscussUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppDiscussUserMapper extends BaseDao<AppDiscussUser> {

    List<String> getUsersByDiscussId(Integer discussId);

    AppDiscussUser getByDiscussIdAndUserId(@Param("discussId") Integer discussId, @Param("userId") String userId);

    int insertAppDiscussUser(AppDiscussUser appDiscussUser);

    int updateAppDiscussUser(AppDiscussUser appDiscussUser);

    int deleteAppDiscussUser(@Param("discussId") Integer discussId, @Param("userId") String userId);

    int deleteUserInDiscussIds(List<Integer> discussIds);

    int deleteDiscussUserInUserIds(List<String> userIds);
}