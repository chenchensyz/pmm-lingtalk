package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppUserMapper;
import cn.com.cybertech.dao.ExternalPushMapper;
import cn.com.cybertech.dao.ExternalToMapper;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.model.ExternalPush;
import cn.com.cybertech.model.ExternalTo;
import cn.com.cybertech.service.ExternalPushService;
import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.RedisUtils;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import cn.com.cybertech.tools.filter.FilterParamUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Service("externalPushService")
@Transactional(readOnly = true)
public class ExternalPushServiceImpl extends BaseServiceImpl implements ExternalPushService {

    @Autowired
    private ExternalPushMapper externalPushMapper;

    @Autowired
    private ExternalToMapper externalToMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    protected JedisPool jedisPool;

    @Override
    public List<ExternalPush> getList(ExternalPush externalpush) {
        return externalPushMapper.getList(externalpush);
    }

    @Override
    @Transactional
    public RestResponse push(RestResponse response, String token, ExternalPush externalpush, Long expire, Boolean offLine) {
        ExternalPush externalPush = externalPushMapper.getExternalPushByUuid(externalpush.getUuid());
        if (externalPush != null) {
            throw new ValueRuntimeException(MessageCode.MESSAGE_ERR_MSGREPART);
        }
        String[] tos = externalpush.getTo().split(",");
        List<String> toIds = Arrays.asList(tos);
        List<String> pushToIds = toIds; //为推送准备的用户列表
        if (StringUtils.isNotBlank(token)) {//应用内im用户 ，根据传入id查询内部id
            AppInfo appInfo = getAppByToken(token);
            pushToIds = appUserMapper.queryAppUserStrByUserId(toIds, appInfo.getId());
            if (toIds == null || toIds.size() <= 0) {
                throw new ValueRuntimeException(MessageCode.USERINFO_ERR_SELECT);
            }
            externalpush.setAppId(appInfo.getId());
        }
        int count1 = externalPushMapper.insertSelective(externalpush);
        if (count1 == 0) {
            throw new ValueRuntimeException(MessageCode.MESSAGE_ERR_SAVE);
        }
        pushMessages(pushToIds, externalpush, offLine); //推送
        int count2 = externalToMapper.addExternalToMore(externalpush.getUuid(), toIds, expire, offLine);
        if (count2 != toIds.size()) {
            throw new ValueRuntimeException(MessageCode.MESSAGE_BODY_ERR_SAVE);
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("uuid", externalpush.getUuid());
        List<ExternalTo> externalTos = externalToMapper.getByUuid(externalpush.getUuid());
        Object externalTosFilter = FilterParamUtil.filterParam(externalTos, FilterParamUtil.EXTERNALTOS_PUSH_FILTER);
        resultMap.put("externalTos", externalTosFilter);
        response.retDatas(resultMap);
        return response;
    }

    public void pushMessages(List<String> toIds, ExternalPush externalpush, Boolean offLine) {
        Jedis jedis = jedisPool.getResource();
        try {
            Set<String> tosSet = new HashSet<String>(toIds);
            for (String to : tosSet) {
                byte[] cByte = CodeUtil.MESSAGE_EVENT_TYPE_EXTERNALPUSH.getBytes(CodeUtil.cs);
                byte[] uByte = to.getBytes(CodeUtil.cs);
                //byte[] dByte = Base64Utils.encode(content.getBytes("UTF-8"));
                byte[] eIdByte = externalpush.getUuid().getBytes(CodeUtil.cs);
                String pushByteStr = offLine != null && offLine == true ? "1" : "0";
                byte[] pushByte = pushByteStr.getBytes(CodeUtil.cs);
                StringBuffer contentBuf = new StringBuffer();
                contentBuf.append(externalpush.getUuid())
                        .append(":").append(RedisUtils.toBase64String(externalpush.getPkg()))
                        .append(":").append(RedisUtils.toBase64String(externalpush.getContent()));
                byte[] dByte = contentBuf.toString().getBytes(CodeUtil.cs);
                jedis.publish("notice".getBytes(), RedisUtils.buildBytesArray(uByte, cByte, eIdByte, pushByte, dByte));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(MessageCode.EXTERNALPUSH_ERR_PUSH);
        } finally {
            jedis.close();
        }
    }


    @Override
    public RestResponse pushstate(RestResponse response, String uuids) {
        List<String> uuidlist = Splitter.on(",").trimResults().splitToList(uuids);
        List<ExternalPush> externalpushs = externalPushMapper.getByuuids(uuidlist);
        if (externalpushs == null || externalpushs.size() <= 0) {
            throw new ValueRuntimeException(MessageCode.MESSAGE_PUSH_ERROR_STAT_NULL);
        }
        List<Map<String, Object>> resultList = Lists.newArrayList();
        for (ExternalPush externalpush : externalpushs) {
            Map<String, Object> resultMap = Maps.newHashMap();
            resultMap.put("uuid", externalpush.getUuid());
            List<ExternalTo> externalTos = externalToMapper.getByUuid(externalpush.getUuid());
            Object externalTosFilter = FilterParamUtil.filterParam(externalTos, FilterParamUtil.EXTERNALTO_FILTER);
            resultMap.put("externalTos", externalTosFilter);
            resultList.add(resultMap);
        }
        response.retDatas(resultList);
        return response;
    }

    @Override
    public RestResponse pushdetail(RestResponse response, String uuid, String by) {
        ExternalPush externalpush = externalPushMapper.getExternalPushByUuid(uuid);
        if (externalpush == null) {
            throw new ValueRuntimeException(MessageCode.MESSAGE_PUSH_ERROR_STAT_NULL);
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("uuid", externalpush.getUuid());
        resultMap.put("from", externalpush.getFrom());
        resultMap.put("pkg", externalpush.getPkg());
        resultMap.put("content", externalpush.getContent());
        List<ExternalTo> externalTos = externalToMapper.getByUuidAndTo(externalpush.getUuid(), by);
        resultMap.put("externalTos", externalTos);
        response.retDatas(resultMap);
        return response;
    }
}
