package cn.com.cybertech.service;

import cn.com.cybertech.model.AppUser;

import java.util.List;

public interface AppUserService {

    List<AppUser> getAppUserList(AppUser appUser);

    void addOrEditAppUser(AppUser appUser);

    int deleteAppUser(String userId);
}