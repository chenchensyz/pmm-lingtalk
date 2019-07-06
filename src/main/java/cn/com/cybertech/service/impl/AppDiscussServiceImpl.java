package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppDiscussMapper;
import cn.com.cybertech.dao.AppDiscussUserMapper;
import cn.com.cybertech.dao.AppInfoMapper;
import cn.com.cybertech.dao.AppUserMapper;
import cn.com.cybertech.model.AppDiscuss;
import cn.com.cybertech.model.AppDiscussUser;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.model.AppUser;
import cn.com.cybertech.model.common.NoticeActionType;
import cn.com.cybertech.service.AppDiscussService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.RedisUtils;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Service("appDiscussService")
public class AppDiscussServiceImpl extends BaseServiceImpl implements AppDiscussService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Autowired
    private AppDiscussMapper appDiscussMapper;

    @Autowired
    private AppDiscussUserMapper appDiscussUserMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private JedisPool jedisPool;

    //通过appId和userId组成sdk的im用户id
    public String createAppUserId(String userId, Integer appId) {
        AppInfo appInfo = appInfoMapper.getAppInfoById(appId); //验证应用
        if (appInfo == null) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_SELECT);
        }
        String newId = userId + "@" + appInfo.getAppId();
        return newId;
    }

    @Override
    public AppDiscuss selectAppDiscussById(Integer discussId) {
        return null;
    }

    @Override
    @Transactional
    public Integer addOrEditAppDiscuss(AppDiscuss appDiscuss) {
        int count;
        List<String> pushUsers; //推送的成员列表
        if (appDiscuss.getDiscussId() == null) {  //新增
            String creatorId = createAppUserId(appDiscuss.getCreatorId(), appDiscuss.getAppId()); //组装新的id
            AppUser appUser = appUserMapper.getAppUserById(creatorId); //查询用户是否存在
            if (appUser == null) {
                throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT);
            }
            appDiscuss.setCreatorId(creatorId);
            appDiscuss.setDisabled(0);
            appDiscuss.setDiscussVersion(1);
            appDiscuss.setCreateTime(new Date());
            count = appDiscussMapper.insertAppDiscuss(appDiscuss);
            if (count == 0) {
                throw new ValueRuntimeException(MessageCode.DISCUSS_ERR_SAVE);
            }
            Set<String> userSet = Sets.newHashSet(creatorId);
            if (appDiscuss.getUserList() != null && appDiscuss.getUserList().size() > 0) {
                for (String userId : appDiscuss.getUserList()) {
                    userSet.add(createAppUserId(userId, appDiscuss.getAppId()));
                }
            }
            appDiscuss.setUserList(new ArrayList<>(userSet));
            int count2 = appDiscussMapper.addDiscussInUserIds(appDiscuss);//将创建者存入成员表
            if (count2 != userSet.size()) {
                throw new ValueRuntimeException(MessageCode.DISCUSS_USER_ERR_SAVE);
            }
            pushUsers = appDiscuss.getUserList();
        } else {
            appDiscuss.setUpdateTime(new Date());
            count = appDiscussMapper.updateAppDiscuss(appDiscuss);
            if (count == 0) {
                throw new ValueRuntimeException(MessageCode.DISCUSS_ERR_SAVE);
            }
            pushUsers = appDiscussUserMapper.getUsersByDiscussId(appDiscuss.getDiscussId()); //查询讨论组成员
        }
        publishDiscussUsersChange(pushUsers, appDiscuss.getDiscussId(), NoticeActionType.add);
        return appDiscuss.getDiscussId();
    }

    //讨论组列表查询
    @Override
    public RestResponse getAppDiscussList(AppDiscuss appDiscuss) {
        RestResponse restResponse = new RestResponse();
        int total = appDiscussMapper.getAppDiscussCount(appDiscuss);   //讨论组总数
        int lastPage = 0;
        if (total > 0) {
            appDiscuss.setPageNum((appDiscuss.getPageNum() - 1) * appDiscuss.getPageSize());
            List<Integer> discussIds = appDiscussMapper.getAppDiscussIdList(appDiscuss); //分页查询出discussIds
            List<AppDiscuss> appDiscussMemberList = appDiscussMapper.getAppDiscussList(discussIds);
            int count = appDiscussMemberList.size();
            lastPage = total / count;
            if (total / count > 0) {
                lastPage += 1;
            }
            restResponse.setData(appDiscussMemberList);
        }
        restResponse.setTotal(Long.valueOf(total));
        restResponse.setPage(lastPage);
        restResponse.setCode(MessageCode.BASE_SUCC_CODE);
        return restResponse;
    }

    @Override
    @Transactional
    public void deleteAppDiscuss(String checkedIds) {
        String[] idArray = checkedIds.split(",");
        List<Integer> discussIds = Lists.newArrayList();
        for (String id : idArray) {
            discussIds.add(Integer.valueOf(id));
        }
        appDiscussUserMapper.deleteUserInDiscussIds(discussIds); //删除成员
        int count = appDiscussMapper.updateAppDiscussDisabled(discussIds);//批量修改为已删除
        if (count != discussIds.size()) {
            throw new ValueRuntimeException(MessageCode.DISCUSS_ERR_DEL);
        }
    }

    @Override
    @Transactional
    public void addAppDiscussUser(Integer appId, Integer discussId, String userId) {
        AppInfo appInfo = appInfoMapper.getAppInfoById(appId); //验证应用
        if (appInfo == null) {
            throw new ValueRuntimeException(MessageCode.APPINFO_ERR_SELECT);
        }
        String pmUserId = createAppUserId(userId, appId);
        AppUser appUser = appUserMapper.getAppUserById(pmUserId); //查询用户是否存在
        if (appUser == null) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT);
        }
        AppDiscuss appDiscuss = appDiscussMapper.getAppDiscussById(discussId); //验证讨论组存在
        if (appDiscuss == null) {
            throw new ValueRuntimeException(MessageCode.DISCUSS_NULL_SELECT); //不存在
        }
        if (appDiscuss.getUserList().contains(pmUserId)) { //成员已存在
            throw new ValueRuntimeException(MessageCode.DISCUSS_USER_EXIT);
        }
        AppDiscussUser appDiscussUser = new AppDiscussUser();
        appDiscussUser.setDiscussId(discussId);
        appDiscussUser.setUserId(pmUserId);
        appDiscussUser.setAdded(appDiscuss.getCreatorId());
        appDiscussUser.setAddTime(new Date());
        int count = appDiscussUserMapper.insertAppDiscussUser(appDiscussUser);//存入成员表
        if (count == 0) {//保存讨论组成员失败
            throw new ValueRuntimeException(MessageCode.DISCUSS_USER_ERR_SAVE);
        }
        appDiscuss.getUserList().add(pmUserId);//推送
        publishDiscussUsersChange(appDiscuss.getUserList(), appDiscuss.getDiscussId(), NoticeActionType.change);
    }

    @Override
    @Transactional
    public void delAppDiscussUser(Integer appId, Integer discussId, String userId) {
        String pmUserId = createAppUserId(userId, appId); //验证应用,组成pmId
        List<String> userList = appDiscussUserMapper.getUsersByDiscussId(discussId); //查询成员
        int count = appDiscussUserMapper.deleteAppDiscussUser(discussId, pmUserId);
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.DISCUSS_USER_ERR_DEL); //删除讨论组成员失败
        }
        publishDiscussUsersChange(userList, discussId, NoticeActionType.change);
    }

    //推送
    private void publishDiscussUsersChange(List<String> userList, Integer discussId, NoticeActionType action) {
        if (userList == null || userList.isEmpty()) {
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            StringBuffer dBuff = new StringBuffer();
            dBuff.append(Base64.encodeBase64String(discussId.toString().getBytes(CodeUtil.cs)));
            dBuff.append(":").append(action.name());

            byte[] cByte = "discuss_change".getBytes();
            byte[] dByte = dBuff.toString().getBytes();

            for (String userId : userList) {
                byte[] uByte = userId.getBytes();
                byte[] eIdByte = discussId.toString().getBytes(CodeUtil.cs);
                byte[] pushByte = "0".getBytes();
                jedis.publish("notice".getBytes(), RedisUtils.buildBytesArray(uByte, cByte, eIdByte, pushByte, dByte));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    @Override
    public RestResponse addAppApiDiscuss(RestResponse response, String token, AppDiscuss appDiscuss) {
        AppInfo appInfo = getAppByToken(token);
        appDiscuss.setAppId(appInfo.getId());
        Integer discussId = addOrEditAppDiscuss(appDiscuss);
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("discussId", discussId);
        response.retDatas(resultMap);
        return response;
    }

    @Override
    public void deleteAppApiDiscuss(String token, Integer discussId) {
        AppInfo appInfo = getAppByToken(token);
        AppDiscuss appDiscuss = appDiscussMapper.getAppDiscussById(discussId);
        if (appDiscuss != null && appDiscuss.getAppId() == appInfo.getId()) {
            deleteAppDiscuss(String.valueOf(discussId));
        } else {
            throw new ValueRuntimeException(MessageCode.DISCUSS_NULL_SELECT);
        }
    }

    @Override
    public void updateAppApiDiscuss(String token, String param) {
        AppInfo appInfo = getAppByToken(token);
        JSONObject jsonObject = JSONObject.parseObject(param);
        AppDiscuss appDiscuss = appDiscussMapper.getAppDiscussById(jsonObject.getInteger("discussId"));
        if (appDiscuss != null && appDiscuss.getAppId() == appInfo.getId()) {
            appDiscuss.setDiscussName(jsonObject.getString("discussName"));
            int count = appDiscussMapper.updateAppDiscuss(appDiscuss);
            if (count == 0) {
                throw new ValueRuntimeException(MessageCode.DISCUSS_USER_ERR_SAVE);
            }
        } else {
            throw new ValueRuntimeException(MessageCode.DISCUSS_NULL_SELECT);
        }
    }

    @Override
    public RestResponse queryMembers(RestResponse response, String token, String param) {
        AppInfo appInfo = getAppByToken(token);
        List<Integer> discussIdList = null;
        String userId = null;
        if (StringUtils.isNotBlank(param)) {
            JSONObject jsonObject = JSONObject.parseObject(param);
            discussIdList = JSONObject.parseObject("discussIdList", List.class);
            userId = jsonObject.getString("userId");
        }
        List<AppDiscuss> list = appDiscussMapper.getAppApiDiscussList(discussIdList, userId, appInfo.getId());
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("discuss", list);
        response.retDatas(resultMap);
        return response;
    }

    @Override
    @Transactional
    public void addMembers(String token, String param) {
        AppInfo appInfo = getAppByToken(token);
        AppDiscuss appDiscuss = JSONObject.parseObject(param, AppDiscuss.class);
        if (appDiscuss != null && appDiscuss.getUserList() != null && appDiscuss.getUserList().size() > 0) {
            AppDiscuss discuss = appDiscussMapper.getAppDiscussById(appDiscuss.getDiscussId());  //查询讨论组
            if (discuss == null || discuss.getAppId() != appInfo.getId()) {
                throw new ValueRuntimeException(MessageCode.DISCUSS_NULL_SELECT);
            }
            Set<String> userList = new HashSet(appDiscuss.getUserList());
            for (String userId : userList) {
                addAppDiscussUser(appInfo.getId(), appDiscuss.getDiscussId(), userId);
            }
        } else {
            throw new ValueRuntimeException(MessageCode.BASE_PARAMS_ERR_VALIDE);
        }
    }

    @Override
    @Transactional
    public void deleteMembers(String token, String param) {
        AppInfo appInfo = getAppByToken(token);
        AppDiscuss appDiscuss = JSONObject.parseObject(param, AppDiscuss.class);
        if (appDiscuss != null && appDiscuss.getUserList() != null && appDiscuss.getUserList().size() > 0) {
            AppDiscuss discuss = appDiscussMapper.getAppDiscussById(appDiscuss.getDiscussId());  //查询讨论组
            if (discuss.getAppId() != appInfo.getId()) {
                throw new ValueRuntimeException(MessageCode.DISCUSS_NULL_SELECT);
            }
            Set<String> userList = new HashSet(appDiscuss.getUserList());
            for (String userId : userList) {
                delAppDiscussUser(appInfo.getId(), appDiscuss.getDiscussId(), userId);
            }
        } else {
            throw new ValueRuntimeException(MessageCode.BASE_PARAMS_ERR_VALIDE);
        }
    }
}