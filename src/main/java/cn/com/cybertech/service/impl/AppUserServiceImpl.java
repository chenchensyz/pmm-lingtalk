package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppDiscussMapper;
import cn.com.cybertech.dao.AppDiscussUserMapper;
import cn.com.cybertech.dao.AppInfoMapper;
import cn.com.cybertech.dao.AppUserMapper;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.model.AppUser;
import cn.com.cybertech.service.AppUserService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("appUserService")
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Autowired
    private AppDiscussUserMapper appDiscussUserMapper;

    @Autowired
    private AppDiscussMapper appDiscussMapper;

    @Override
    public List<AppUser> getAppUserList(AppUser appUser) {
        return appUserMapper.getAppUserList(appUser);
    }

    @Override
    public void addOrEditAppUser(AppUser appUser) {
        AppInfo appInfo = appInfoMapper.getAppInfoById(appUser.getAppId());
        if (appInfo == null) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_SELECT);
        }

        if (StringUtils.isBlank(appUser.getId())) {  //新增
            appUser.setId(createAppUserId(appUser.getUserId(), appInfo.getAppId())); //组装新的id
            AppUser user = appUserMapper.getAppUserById(appUser.getId());
            if (user != null) {
                throw new ValueRuntimeException(MessageCode.USERINFO_EXIST);
            }
            appUser.setUserState(CodeUtil.PMUSER_STATE_ACTIVATED);
            appUser.setRoleId(CodeUtil.DEFAULT_ROLE_ID);
            appUser.setDisabled(0);
            appUser.setUserState(1);
            appUser.setCreateTime(new Date());
            appUserMapper.insertAppUser(appUser);
        } else {
            appUserMapper.updateAppUser(appUser);
        }
    }

    @Override
    @Transactional
    public void deleteAppUsers(String checkedIds) {
        if (StringUtils.isBlank(checkedIds.trim())) {
            throw new ValueRuntimeException(MessageCode.BASE_PARAMS_ERR_VALIDE);
        }
        List<String> userIds = Arrays.asList(checkedIds.split(","));
        appDiscussUserMapper.deleteDiscussUserInUserIds(userIds);   //从讨论组中删除
        appDiscussMapper.deleteDiscussInCreatorIds(userIds); //删除用户创建的讨论组
        int count = appUserMapper.deleteAppUserInIds(userIds);
        if (count != userIds.size()) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_DEL);
        }
    }

    //通过appId和userId组成sdk的im用户id
    public String createAppUserId(String userId, String appId) {
        String newId = userId + "@" + appId;
        return newId;
    }
}