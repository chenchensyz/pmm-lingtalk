package cn.com.cybertech.service;

import cn.com.cybertech.model.WebUser;

import java.util.List;
import java.util.Map;

public interface WebUserService {

    List<WebUser> getWebUserList(WebUser webUser);

    WebUser getWebUserByPhone(String phone, Integer companyId);

    Map<String, Object> login(WebUser webUser, String platform);

    //注册用户
    void registerUser(WebUser webUser, String companyName, String introduction);

    //管理员添加用户
    void addOrEdidUser(WebUser webUser);
}
