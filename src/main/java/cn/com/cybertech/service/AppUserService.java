package cn.com.cybertech.service;

import cn.com.cybertech.model.AppUser;
import cn.com.cybertech.tools.RestResponse;

import java.util.List;
import java.util.Map;

public interface AppUserService {

    List<AppUser> getAppUserList(AppUser appUser);

    void addOrEditAppUser(AppUser appUser);

    void deleteAppUsers(List<String> userIds);

    RestResponse addAppApiUser(RestResponse response,String token, Map<String, Object> paramMap);

    void deleteAppApiUser(String token, Map<String, Object> paramMap);

    RestResponse queryAppApiUser(RestResponse response, String token, Map<String, Object> paramMap);

}