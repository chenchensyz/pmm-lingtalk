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
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("appUserService")
public class AppUserServiceImpl extends BaseServiceImpl implements AppUserService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Autowired
    private AppDiscussUserMapper appDiscussUserMapper;

    @Autowired
    private AppDiscussMapper appDiscussMapper;

    //通过appId和userId组成sdk的im用户id
    public String createAppUserId(String userId, String appId) {
        String newId = userId + "@" + appId;
        return newId;
    }

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
    public void deleteAppUsers(List<String> userIds) {
        appDiscussUserMapper.deleteDiscussUserInUserIds(userIds);   //从讨论组中删除
        appDiscussMapper.deleteDiscussInCreatorIds(userIds); //删除用户创建的讨论组
        int count = appUserMapper.deleteAppUserInIds(userIds);
        if (count != userIds.size()) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_DEL);
        }
    }

    @Override
    public RestResponse addAppApiUser(RestResponse response, String token, Map<String, Object> paramMap) {
        AppInfo appInfo = getAppByToken(token);
        if (paramMap.isEmpty()) { //传入参数
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_MESSAGEBODY);
        }
        List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
        if (paramMap.get("users") == null) {
            throw new ValueRuntimeException(MessageCode.APPINFO_SERVER_ERR_USERS_NULL);
        }
        List<Map<String, String>> userList = (ArrayList) paramMap.get("users");
        Iterator<Map<String, String>> ite = userList.iterator();
        while (ite.hasNext()) {
            Map<String, String> u = ite.next();
            String userId = u.get("userId");
            String newId = createAppUserId(userId, appInfo.getAppId());
            AppUser user = appUserMapper.getAppUserById(newId);//查询用户是否存在
            if (user != null) {
                throw new ValueRuntimeException(MessageCode.USERINFO_EXIST);
            }
            //查询用户是否已存在
            AppUser appUser = new AppUser();
            appUser.setId(newId);
            appUser.setUserId(userId);
            appUser.setUserName(u.get("userName"));
            appUser.setUserPasswd(u.get("password"));
            appUser.setAppId(appInfo.getId());
            appUser.setDisabled(0);
            appUser.setCompanyId(appInfo.getCompanyId());
            appUser.setUserState(CodeUtil.PMUSER_STATE_ACTIVATED);
            appUser.setRoleId(CodeUtil.DEFAULT_ROLE_ID);
            appUser.setCreateTime(new Date());
            int count2 = appUserMapper.insertAppUser(appUser);
            if (count2 != 1) {
                throw new ValueRuntimeException(MessageCode.USERINFO_ERR_ADD);
            }
            Map<String, Object> userMap = Maps.newHashMap();
            userMap.put("userId", userId);
            userMap.put("userName", appUser.getUserName());
            userMap.put("password", appUser.getUserPasswd());
            users.add(userMap);
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("users", users);
        response.retDatas(resultMap);
        return response;
    }


    @Override
    @Transactional
    public void deleteAppApiUser(String token, Map<String, Object> paramMap) {
        AppInfo appInfo = getAppByToken(token);
        List<String> userIds =
                paramMap.get("userIds") == null ? null : (ArrayList) paramMap.get("userIds");
        if (userIds == null || userIds.size() <= 0) {

        }
        List<String> checkedIds = Lists.newArrayList();
        for (String userId : userIds) {
            String newId = createAppUserId(userId, appInfo.getAppId());
            checkedIds.add(newId);
        }
        deleteAppUsers(checkedIds);
    }

    @Override
    public RestResponse queryAppApiUser(RestResponse response, String token, Map<String, Object> paramMap) {
        AppInfo appInfo = getAppByToken(token);
        AppUser appUser = new AppUser();
        appUser.setAppId(appInfo.getId());
        List<AppUser> appUsers = appUserMapper.getAppUserList(appUser);
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("users", appUsers);
        response.retDatas(resultMap);
        return response;
    }
}