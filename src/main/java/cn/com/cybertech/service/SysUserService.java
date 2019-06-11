package cn.com.cybertech.service;

import cn.com.cybertech.model.SysUser;

import java.util.List;
import java.util.Map;

public interface SysUserService {

    List<SysUser> getSysUserList(String token, SysUser sysUser);

    SysUser getSysUserByUserName(String userName);

    Map<String, Object> login(SysUser sysUser, String platform);


    //管理员添加用户
    void addOrEditUser(String token, String platform, SysUser sysUser);

    //管理员删除用户
    void optionUser(String platform, Long userId, Integer state);

    SysUser getUserInfo(String token);

    //修改密码
    void resetPassword(String token, String oldPassword, String newPassword);
}
