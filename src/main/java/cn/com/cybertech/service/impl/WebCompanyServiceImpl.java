package cn.com.cybertech.service.impl;

import cn.com.cybertech.config.redis.RedisTool;
import cn.com.cybertech.dao.WebCompanyMapper;
import cn.com.cybertech.dao.WebUserMapper;
import cn.com.cybertech.model.SysUser;
import cn.com.cybertech.model.WebCompany;
import cn.com.cybertech.model.WebUser;
import cn.com.cybertech.service.WebCompanyService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("webCompanyService")
public class WebCompanyServiceImpl implements WebCompanyService {

    @Autowired
    private WebCompanyMapper webCompanyMapper;

    @Autowired
    private WebUserMapper webUserMapper;

    @Autowired
    private RedisTool redisTool;

    @Override
    public void saveWebCompany(String token, WebCompany webCompany) {
        WebUser webUser = webUserMapper.getWebUserLoginPass(webCompany.getOwner());
        if (webUser == null) {
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT);
        }
        SysUser localUser = redisTool.getSysUser(CodeUtil.REDIS_PREFIX + token);
        webCompany.setUpdateUser(localUser.getUserName());
        int count = webCompanyMapper.updateWebCompany(webCompany);
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.COMPANYINFO_ERR_SAVE);
        }
    }

    @Override
    public List<WebCompany> getWebCompanyByUserName(String userName) {
        return webCompanyMapper.getWebCompanyByUserName(userName);
    }

    @Override
    public List<WebCompany> getWebCompanyList(WebCompany webCompany) {
        return webCompanyMapper.getWebCompanyList(webCompany);
    }

    @Override
    @Transactional
    public void changeCompany(Integer id, Integer state) {
        WebCompany webCompany = webCompanyMapper.getWebCompanyById(id);
        if (webCompany == null) {
            throw new ValueRuntimeException(MessageCode.COMPANYINFO_ERR_SELECT);
        }
        int count;
        if (state == -1) {
            webUserMapper.deleteUserByCompanyId(webCompany.getId()); //删除公司下的用户
            if (webCompany == null) {
                throw new ValueRuntimeException(MessageCode.COMPANYINFO_ERR_SELECT);
            }
            count = webCompanyMapper.deleteWebCompanyById(id);
        } else {
            webCompany.setState(state);
            count = webCompanyMapper.updateWebCompany(webCompany);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.COMPANYINFO_ERR_DELECT);
        }
    }
}