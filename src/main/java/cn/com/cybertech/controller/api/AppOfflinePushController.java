package cn.com.cybertech.controller.api;
/**
 * 离线推送接口
 */

import cn.com.cybertech.tools.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/app/api/offlinepush")
public class AppOfflinePushController {

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private Environment env;

    //注册推送
    @RequestMapping("/register")
    public RestResponse registerOffline(String userId, String appId, String platform, String apkName,
                                        String manufacture, String deviceToken) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", userId + "@" + appId);
        jsonObject.put("pushtype", messageCodeUtil.getMessage(manufacture));
        jsonObject.put("deviceToken", deviceToken);
        if (CodeUtil.CERT_IOS.equals(manufacture)) {
            jsonObject.put("bundleid", apkName);
        } else {
            jsonObject.put("apkname", apkName);
        }
        int msgCode = MessageCode.OFFLINEPUSH_ERR_REGISTER;
        String msg;
        String upload_url = env.getProperty(CodeUtil.CERT_UPLOAD_URL);
        String push_status_url = env.getProperty(CodeUtil.PUSH_STATUS_URL);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        Map<String, Object> map = HttpClientUtil.httpRequest(upload_url + push_status_url, CodeUtil.METHOD_POST,
                CodeUtil.CONTEXT_JSON, jsonArray.toString());
        if (map.get("code") != null && CodeUtil.HTTP_OK == (Integer) map.get("code")) {
            msgCode = MessageCode.BASE_SUCC_CODE;
            msg = messageCodeUtil.getMessage(msgCode);
        } else {
            msg = JSON.toJSONString(RestResponse.res(MessageCode.OFFLINEPUSH_ERR_REGISTER, messageCodeUtil.getMessage(MessageCode.OFFLINEPUSH_ERR_REGISTER) + map.get("error")));
        }
        RestResponse response = new RestResponse();
        response.retMsg(response, msgCode, msg);
        return response;
    }

    // 注销推送
    @RequestMapping("/unregister")
    public RestResponse unregisterOffline(String userId, String appId) {
        int msgCode = MessageCode.OFFLINEPUSH_ERR_UNREGISTER;
        String msg;
        String upload_url = env.getProperty(CodeUtil.CERT_UPLOAD_URL);
        String push_status_url = env.getProperty(CodeUtil.PUSH_STATUS_URL);
        String requestUrl = upload_url + push_status_url + "/" + userId + "@" + appId;
        Map<String, Object> map =
                HttpClientUtil.httpRequest(requestUrl, CodeUtil.METHOD_DELETE, null, null);
        if (map.get("code") != null && CodeUtil.HTTP_OK == (Integer) map.get("code")) {
            msgCode = MessageCode.BASE_SUCC_CODE;
            msg = messageCodeUtil.getMessage(msgCode);
        } else {
            msg = JSON.toJSONString(RestResponse.res(MessageCode.OFFLINEPUSH_ERR_REGISTER, messageCodeUtil.getMessage(MessageCode.OFFLINEPUSH_ERR_REGISTER) + map.get("error")));
        }
        RestResponse response = new RestResponse();
        response.retMsg(response, msgCode, msg);
        return response;
    }
}
