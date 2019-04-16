package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.WebCompanyMapper;
import cn.com.cybertech.model.WebCompany;
import cn.com.cybertech.service.WebCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("webCompanyService")
public class WebCompanyServiceImpl implements WebCompanyService {

    @Autowired
    private WebCompanyMapper webCompanyMapper;

    @Override
    public void saveWebCompany(WebCompany webCompany) {
        webCompanyMapper.insertSelective(webCompany);
    }

    @Override
    public List<WebCompany> selectWebCompanyByPhone(String phone) {
        return webCompanyMapper.selectWebCompanyByPhone(phone);
    }
}