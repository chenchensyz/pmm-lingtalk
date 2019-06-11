package cn.com.cybertech.controller;

import cn.com.cybertech.model.WebCompany;
import cn.com.cybertech.service.WebCompanyService;
import cn.com.cybertech.tools.RestResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/company")
public class WebCompanyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebCompanyController.class);

    @Autowired
    private WebCompanyService webCompanyService;

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
}
