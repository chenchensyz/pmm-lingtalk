package cn.com.cybertech.service;

import cn.com.cybertech.model.WebUser;

import java.util.List;
import java.util.Map;

public interface WebUserService {

    List<WebUser> getWebUserList(WebUser webUser);

    WebUser getWebLoginByPhone(String phone, Integer companyId);

    Map<String,Object> login(WebUser webUser, String platform);

    void saveUser(WebUser webUser, String companyName,String introduction);
}
