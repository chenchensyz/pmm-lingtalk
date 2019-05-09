package cn.com.cybertech.service;

import cn.com.cybertech.model.AppDiscuss;
import cn.com.cybertech.tools.RestResponse;

public interface AppDiscussService {

    AppDiscuss selectAppDiscussById(Integer discussId);

    void addOrEditAppDiscuss(AppDiscuss appDiscuss);

    RestResponse getAppDiscussList(AppDiscuss appDiscuss);

    void addAppDiscussUser(Integer discussId, String userId);

    void delAppDiscussUser(Integer discussId, String userId);
}