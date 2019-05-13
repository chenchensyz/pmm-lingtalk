package cn.com.cybertech.service;

import cn.com.cybertech.model.AppDiscuss;
import cn.com.cybertech.tools.RestResponse;

public interface AppDiscussService {

    AppDiscuss selectAppDiscussById(Integer discussId);

    void addOrEditAppDiscuss(AppDiscuss appDiscuss);

    RestResponse getAppDiscussList(AppDiscuss appDiscuss);

    void deleteAppDiscuss(Integer appId, Integer discussId);

    void addAppDiscussUser(Integer appId, Integer discussId, String userId);

    void delAppDiscussUser(Integer appId, Integer discussId, String userId);
}