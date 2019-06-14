package cn.com.cybertech.service;

import cn.com.cybertech.model.ExternalPush;
import cn.com.cybertech.tools.RestResponse;

import java.util.List;

public interface ExternalPushService {


    List<ExternalPush> getList(ExternalPush externalpush);

    RestResponse push(RestResponse response, String token, ExternalPush externalpush, Long expire, Boolean offLine);

    RestResponse pushstate(RestResponse response, String uuids);

    RestResponse pushdetail(RestResponse response, String uuid, String by);

}
