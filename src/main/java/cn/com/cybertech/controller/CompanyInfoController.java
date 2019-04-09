package cn.com.cybertech.controller;

import cn.com.cybertech.model.CompanyInfo;
import cn.com.cybertech.service.CompanyInfoService;
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
@RequestMapping("/company")
public class CompanyInfoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyInfoController.class);

    @Autowired
    private CompanyInfoService companyInfoService;

    @RequestMapping(value = "/list/{phone}", method = RequestMethod.GET)
    public RestResponse getCompanyList(@PathVariable("phone") String phone) {
        List<CompanyInfo> companyInfos = companyInfoService.selectCompanyByPhone(phone);
        return RestResponse.success().setData(companyInfos);
    }

}
