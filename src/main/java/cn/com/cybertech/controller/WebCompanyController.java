package cn.com.cybertech.controller;

import cn.com.cybertech.model.WebCompany;
import cn.com.cybertech.service.WebCompanyService;
import cn.com.cybertech.tools.RestResponse;
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

}
