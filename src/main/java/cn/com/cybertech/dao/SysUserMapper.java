package cn.com.cybertech.dao;

import cn.com.cybertech.dao.common.BaseDao;
import cn.com.cybertech.model.SysUser;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper extends BaseDao<SysUser> {

    SysUser getSysUserByUserName(String userName);

    SysUser getSysUserById(Long id);

   int deleteSysUserById(Long id);

    int insertSysUser(SysUser sysUser);

    int updateSysUser(SysUser sysUser);

    int  updateSysUserPassByUserName(@Param("password") String password, @Param("userName") String userName);

}