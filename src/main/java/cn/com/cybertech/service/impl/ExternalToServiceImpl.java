package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.ExternalToMapper;
import cn.com.cybertech.model.ExternalTo;
import cn.com.cybertech.service.ExternalToService;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service("externalToService")
public class ExternalToServiceImpl implements ExternalToService {

    @Autowired
    private ExternalToMapper externalToMapper;

    @Override
    public void pushStateSet(String uuid, String to, Integer state) {
        List<ExternalTo> externalTos = externalToMapper.getByUuidAndTo(uuid, to);
        if (externalTos == null || externalTos.size() <= 0) {
            throw new ValueRuntimeException(MessageCode.EXTERNALPUSH_NULL);
        }
        ExternalTo externalTo = externalTos.get(0);
        externalTo.setState(state);
        externalTo.setAcktime(new Date());
        int count = externalToMapper.updateExternalTo(externalTo);
        if (count == 0) {
            throw new ValueRuntimeException(MessageCode.EXTERNALPUSH_STATE_ERR);
        }
    }

    @Override
    public List<ExternalTo> getPushUserDetail(String to, String appId) {
        return externalToMapper.getPushUserDetail(to, appId);
    }
}
