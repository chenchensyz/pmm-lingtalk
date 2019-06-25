package cn.com.cybertech.controller.sys;

import cn.com.cybertech.model.CommonConfig;
import cn.com.cybertech.service.CommonConfigService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/sys/commonConfig")
public class CommonConfigController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonConfigController.class);

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private CommonConfigService commonConfigService;

    @RequestMapping("/list")
    public RestResponse queryCommonConfigList(CommonConfig commonConfig) {
        PageHelper.startPage(commonConfig.getPageNum(), commonConfig.getPageSize());
        List<CommonConfig> commonConfigs = commonConfigService.getCommonConfigList(commonConfig);
        PageInfo<CommonConfig> commonConfigPage = new PageInfo<CommonConfig>(commonConfigs);
        return RestResponse.success().setData(commonConfigs)
                .setTotal(commonConfigPage.getTotal()).setPage(commonConfigPage.getLastPage());
    }

    @RequestMapping("/addOrEditCommonConfig")
    public RestResponse addOrEditCommonConfig(HttpServletRequest request, CommonConfig commonConfig) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            commonConfigService.addOrEditCommonConfig(token, commonConfig);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping("/deleteCommonConfig")
    public RestResponse deleteCommonConfig(Integer id) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            commonConfigService.deleteCommonConfig(id);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    /**
     * 根据平台获取版本列表
     *
     * @param platform
     * @return
     */
    @RequestMapping("/getVersionList")
    public RestResponse getVersionList(String platform) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        List<String> versions = commonConfigService.getVersionByPlatform(platform);
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode)).setData(versions);
    }

    /**
     * 复制全局配置
     *
     * @param platform
     * @param fromVersion
     * @param toVersion
     * @return
     */
    @RequestMapping("/copyCommonConfig")
    public RestResponse copyCommonConfig(HttpServletRequest request, @RequestParam String platform,
                                         @RequestParam String fromVersion
            , @RequestParam String toVersion) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            commonConfigService.copyCommonConfig(token, platform, fromVersion, toVersion);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

}
