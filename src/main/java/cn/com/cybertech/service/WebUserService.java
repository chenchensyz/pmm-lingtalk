package cn.com.cybertech.service;

import cn.com.cybertech.model.WebUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WebUserService {

    List<WebUser> getWebUserList(String token, WebUser webUser);

    WebUser getWebUserByPhone(String phone, Integer companyId);

    Map<String, Object> login(WebUser webUser, String platform);

    //注册用户
    void registerUser(WebUser webUser, String companyName, String introduction);

    //管理员添加用户
    void addOrEditUser(String token, WebUser webUser);

    //查询：用户绑定应用
    List<Integer> getUserApp(String token, Long userId);

    //管理员删除用户
    void optionUser(String platform, Long userId, Integer state);

    WebUser getUserInfo(String token);

    //修改密码
    void resetPassword(String token, String oldPassword, String newPassword);
}
