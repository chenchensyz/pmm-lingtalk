package cn.com.cybertech.controller.api;

import cn.com.cybertech.model.AppDiscuss;
import cn.com.cybertech.service.AppDiscussService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("app/api/discussInfo")
public class AppDiscussApiController {

    @Autowired
    private AppDiscussService appDiscussService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    /**
     * 创建讨论组`
     */
    @RequestMapping(value = "/create")
    public RestResponse create(HttpServletRequest request,@RequestBody String param) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        AppDiscuss appDiscuss = JSONObject.parseObject(param, AppDiscuss.class);
        try {
            response = appDiscussService.addAppApiDiscuss(response, token, appDiscuss);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

    /**
     * 删除讨论组
     */
    @RequestMapping(value = "/delete")
    public RestResponse delete(HttpServletRequest request, @RequestBody Map<String, Object> paramMap) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        Integer discussId = paramMap.get("discussId") == null ? null : (Integer) paramMap.get("discussId");
        if (discussId == null) {
            msgCode = MessageCode.BASE_PARAMS_ERR_VALIDE;
        } else {
            try {
                appDiscussService.deleteAppApiDiscuss(token, discussId);
            } catch (ValueRuntimeException e) {
                msgCode = (Integer) e.getValue();
            }
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

    /**
     * 更新讨论组
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public RestResponse updateDiscuss(HttpServletRequest request, @RequestBody  String param) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            appDiscussService.updateAppApiDiscuss(token, param);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

    /**
     * 查找用户
     */
    @RequestMapping(value = "/queryMember")
    public RestResponse query(HttpServletRequest request, String param) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            response = appDiscussService.queryMembers(response, token, param);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

    /*讨论组添加成员*/
    @RequestMapping(value = "/addMembers", method = RequestMethod.POST)
    public RestResponse addMembers(HttpServletRequest request, @RequestBody String param) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            appDiscussService.addMembers(token, param);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

    /*讨论组删除成员*/
    @RequestMapping(value = "/deleteMembers", method = RequestMethod.POST)
    public RestResponse deleteMembers(HttpServletRequest request, @RequestBody String param) {
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            appDiscussService.deleteMembers(token, param);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

}
