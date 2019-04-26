package cn.com.cybertech.controller;

import cn.com.cybertech.config.redis.RedisTool;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.model.WebCompany;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebUserService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/web/user")
public class WebUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebUserController.class);

    @Autowired
    private WebUserService webUserService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @Autowired
    private RedisTool redisTool;

    @RequestMapping("/list")
    public RestResponse queryWebUserList(HttpServletRequest request,WebUser webUser) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try{
            WebUser user = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
            webUser.setCompanyId(user.getCompanyId());
            PageHelper.startPage(webUser.getPageNum(), webUser.getPageSize());
            List<WebUser> webUserList = webUserService.getWebUserList(webUser);
            PageInfo<WebUser> webInfoPage = new PageInfo<>(webUserList);
            return RestResponse.success().setData(webUserList)
                    .setTotal(webInfoPage.getTotal()).setPage(webInfoPage.getLastPage());
        }catch (ValueRuntimeException e){
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping("/addOrEdidUser")
    public RestResponse addOrEdidUser(HttpServletRequest request, WebUser webUser) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            WebUser user = redisTool.getUser(CodeUtil.REDIS_PREFIX + token);
            webUser.setCompanyId(user.getCompanyId());
            webUserService.addOrEdidUser(webUser);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }
}
