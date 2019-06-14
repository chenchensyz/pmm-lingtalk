package cn.com.cybertech.service;

import cn.com.cybertech.model.AppDiscuss;
import cn.com.cybertech.tools.RestResponse;

public interface AppDiscussService {

    AppDiscuss selectAppDiscussById(Integer discussId);

    Integer addOrEditAppDiscuss(AppDiscuss appDiscuss);

    RestResponse getAppDiscussList(AppDiscuss appDiscuss);

    void deleteAppDiscuss(String checkedIds);

    void addAppDiscussUser(Integer appId, Integer discussId, String userId);

    void delAppDiscussUser(Integer appId, Integer discussId, String userId);

    //sdk创建
    RestResponse addAppApiDiscuss(RestResponse response, String token, AppDiscuss appDiscuss);

    //sdk删除
    void deleteAppApiDiscuss(String token, Integer discussId);

    //sdk修改名称
    void updateAppApiDiscuss(String token, String param);

    RestResponse queryMembers(RestResponse response, String token, String param);

    //sdk添加成员
    void addMembers(String token, String param);

    //sdk删除成员
    void deleteMembers(String token, String param);
}