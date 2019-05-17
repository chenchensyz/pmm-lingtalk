package cn.com.cybertech.service;

import cn.com.cybertech.model.ExternalTo;

import java.util.List;

public interface ExternalToService {

    void pushStateSet(String uuid, String to, Integer state);

    //查询用户需要离线推送的消息
    List<ExternalTo> getPushUserDetail(String to, String appId);
}
