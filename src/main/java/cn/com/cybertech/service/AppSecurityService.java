package cn.com.cybertech.service;

import cn.com.cybertech.tools.RestResponse;

public interface AppSecurityService {

    RestResponse queryToken(RestResponse response,String appId, String secret);

    RestResponse userlogin(RestResponse response, String appId, String userId, String password, String platform);
}
