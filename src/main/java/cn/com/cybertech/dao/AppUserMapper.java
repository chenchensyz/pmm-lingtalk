package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.AppUser;

import java.util.List;

public interface AppUserMapper extends BaseDao<AppUser> {

    List<AppUser> getAppUserList(AppUser appUser);

    AppUser getAppUserById(String id);

    int insertAppUser(AppUser appUser);

    int updateAppUser(AppUser appUser);

    int deleteAppUserInIds(List<String> ids);

    int getAppUserIdsByAppId(Integer appId);
}