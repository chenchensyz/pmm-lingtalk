package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppDiscussMapper;
import cn.com.cybertech.model.AppDiscuss;
import cn.com.cybertech.service.AppDiscussService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("appDiscussService")
public class AppDiscussServicempl implements AppDiscussService {

    @Autowired
    private AppDiscussMapper appDiscussMapper;


    @Override
    public AppDiscuss selectAppDiscussById(Integer discussId) {
        return null;
    }

    @Override
    public void addOrEditAppDiscuss(AppDiscuss appDiscuss) {
        int count;
        if (appDiscuss.getDiscussId() == null) {  //新增
            appDiscuss.setDisabled(0);
            appDiscuss.setDiscussVersion(1);
            appDiscuss.setCreateTime(new Date());
            count = appDiscussMapper.insertAppDiscuss(appDiscuss);


        } else {
            appDiscuss.setUpdateTime(new Date());
            count = appDiscussMapper.updateAppDiscuss(appDiscuss);
        }
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.DISCUSS_ERR_SAVE);
        }
    }

    @Override
    public RestResponse getAppDiscussList(AppDiscuss appDiscuss) {
        RestResponse restResponse = new RestResponse();
        int total = appDiscussMapper.getAppDiscussCount(appDiscuss);   //讨论组总数
        int lastPage = 0;
        if (total > 0) {
            appDiscuss.setPageNum((appDiscuss.getPageNum() - 1) * appDiscuss.getPageSize());
            List<Integer> discussIds = appDiscussMapper.getAppDiscussIdList(appDiscuss); //分页查询出discussIds
            List<AppDiscuss> appDiscussMemberList = appDiscussMapper.getAppDiscussList(discussIds);
            int count = appDiscussMemberList.size();
            lastPage = total / count;
            if (total / count > 0) {
                lastPage += 1;
            }
            restResponse.setData(appDiscussMemberList);
        }
        restResponse.setTotal(Long.valueOf(total));
        restResponse.setPage(lastPage);
        restResponse.setCode(MessageCode.BASE_SUCC_CODE);
        return restResponse;
    }

    @Override
    public void addAppDiscussUser(Integer discussId, String userId) {

    }

    @Override
    public void delAppDiscussUser(Integer discussId, String userId) {

    }
}