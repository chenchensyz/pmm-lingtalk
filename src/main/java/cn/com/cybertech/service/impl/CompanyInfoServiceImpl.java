package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.CompanyInfoMapper;
import cn.com.cybertech.model.CompanyInfo;
import cn.com.cybertech.service.CompanyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("companyInfoService")
public class CompanyInfoServiceImpl implements CompanyInfoService {

    @Autowired
    private CompanyInfoMapper companyInfoMapper;

    @Override
    public List<CompanyInfo> selectCompanyByPhone(String phone) {
        return companyInfoMapper.selectCompanyByPhone(phone);
    }
}