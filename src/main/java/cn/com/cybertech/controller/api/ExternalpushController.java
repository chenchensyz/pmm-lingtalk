package cn.com.cybertech.controller.api;

import cn.com.cybertech.model.ExternalTo;
import cn.com.cybertech.service.ExternalToService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import cn.com.cybertech.tools.filter.FilterParamUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 第三方系统推送接口
 */
@RestController
@RequestMapping("/app/api/externalpush")
public class ExternalpushController {

    private static final Logger logger = LoggerFactory.getLogger(ExternalpushController.class);

    @Autowired
    private ExternalToService externalToService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    /**
     * 接收方设置推送状态
     */
    @RequestMapping("/pushstateset")
    public RestResponse pushstateset(@RequestParam String uuid, @RequestParam String by, @RequestParam Integer state) {
        logger.info("接收方设置推送状态 uuid:{},state:{}", uuid, state);
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        Map<String, Object> resultMap = Maps.newHashMap();
        try {
            externalToService.pushStateSet(uuid, by, state);
            resultMap.put("uuid", uuid);
            resultMap.put("by", by);
            resultMap.put("state", state);
            response.retDatas(resultMap);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode));
        return response;
    }

    /**
     * 查询用户需要离线推送的消息
     */
    @RequestMapping("/queryPushUserDetail")
    public RestResponse queryPushUserDetail(String to, String appId) {
        logger.debug("查询用户需要离线推送的消息 to:{}", to);
        RestResponse response = new RestResponse();
        int msgCode = MessageCode.BASE_SUCC_CODE;
        List<ExternalTo> pushUserDetail = externalToService.getPushUserDetail(to, appId);
        Object externalToFilter = FilterParamUtil.filterParam(pushUserDetail, FilterParamUtil.EXTERNALTO_FILTER);
        response.retMsg(response, msgCode, messageCodeUtil.getMessage(msgCode)).retDatas(externalToFilter);
        return response;
    }
}