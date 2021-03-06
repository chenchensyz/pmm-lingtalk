package cn.com.cybertech.controller.app;


import cn.com.cybertech.model.AppUser;
import cn.com.cybertech.service.AppUserService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/app/user")
public class AppUserController {


    @Autowired
    private AppUserService appUserService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestResponse queryAppCertList(AppUser appUser) {
        PageHelper.startPage(appUser.getPageNum(), appUser.getPageSize());
        List<AppUser> appUserList = appUserService.getAppUserList(appUser);
        PageInfo<AppUser> appUserPage = new PageInfo<AppUser>(appUserList);
        return RestResponse.success().setData(appUserList)
                .setTotal(appUserPage.getTotal()).setPage(appUserPage.getLastPage());
    }

    @RequestMapping(value = "/addOrEditAppUser")
    public RestResponse addOrEditAppUser(HttpServletRequest request, AppUser appUser) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            appUserService.addOrEditAppUser(appUser);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    //删除IM用户
    @RequestMapping("/delAppUsers")
    public RestResponse delAppUsers(String checkedIds) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            if (StringUtils.isBlank(checkedIds.trim())) {
                throw new ValueRuntimeException(MessageCode.BASE_PARAMS_ERR_VALIDE);
            }
            List<String> userIds = Arrays.asList(checkedIds.split(","));
            appUserService.deleteAppUsers(userIds);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }

        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

}
