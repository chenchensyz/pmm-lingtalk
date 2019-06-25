package cn.com.cybertech.controller;

import cn.com.cybertech.model.WebCompany;
import cn.com.cybertech.service.WebCompanyService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.MessageCodeUtil;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/web/company")
public class WebCompanyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebCompanyController.class);

    @Autowired
    private WebCompanyService webCompanyService;

    @Autowired
    private MessageCodeUtil messageCodeUtil;

    //查询用户所属公司
    @RequestMapping(value = "/list/{userName}", method = RequestMethod.GET)
    public RestResponse getCompanyList(@PathVariable("userName") String userName) {
        List<WebCompany> companyInfos = webCompanyService.getWebCompanyByUserName(userName);
        return RestResponse.success().setData(companyInfos);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestResponse getCompanyList(WebCompany webCompany) {
        PageHelper.startPage(webCompany.getPageNum(), webCompany.getPageSize());
        List<WebCompany> companyInfos = webCompanyService.getWebCompanyList(webCompany);
        PageInfo<WebCompany> companyInfoPage = new PageInfo<>(companyInfos);
        return RestResponse.success().setData(companyInfos)
                .setTotal(companyInfoPage.getTotal()).setPage(companyInfoPage.getLastPage());
    }

    @RequestMapping(value = "/saveCompany", method = RequestMethod.POST)
    public RestResponse saveCompany(HttpServletRequest request, WebCompany webCompany) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        String token = request.getHeader("token");
        try {
            webCompanyService.saveWebCompany(token, webCompany);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }

    @RequestMapping(value = "/changeCompany", method = RequestMethod.POST)
    public RestResponse changeCompany(Integer id, Integer state) {
        int msgCode = MessageCode.BASE_SUCC_CODE;
        try {
            webCompanyService.changeCompany(id, state);
        } catch (ValueRuntimeException e) {
            msgCode = (Integer) e.getValue();
        }
        return RestResponse.res(msgCode, messageCodeUtil.getMessage(msgCode));
    }
}
