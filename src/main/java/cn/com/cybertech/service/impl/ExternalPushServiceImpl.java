package cn.com.cybertech.service.impl;

import cn.com.cybertech.dao.AppUserMapper;
import cn.com.cybertech.dao.ExternalPushMapper;
import cn.com.cybertech.dao.ExternalToMapper;
import cn.com.cybertech.model.AppInfo;
import cn.com.cybertech.model.ExternalPush;
import cn.com.cybertech.model.ExternalTo;
import cn.com.cybertech.service.ExternalPushService;
import cn.com.cybertech.tools.*;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import cn.com.cybertech.tools.filter.FilterParamUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
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

    @Autowired
    private Environment env;

    @Override
    public List<ExternalPush> getList(ExternalPush externalpush) {
        return externalPushMapper.getList(externalpush);
    }

    @Override
    @Transactional
    public RestResponse push(RestResponse response, String token, ExternalPush externalpush, Long expire, Integer offLine) {
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

    public void pushMessages(List<String> toIds, ExternalPush externalpush, Integer offLine) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_USER_DBINDEX);  //选择4号库查询在线状态
        try {
            Set<String> tosSet = new HashSet<String>(toIds);
            for (String to : tosSet) {
                onlinePush(externalpush, jedis, to);  //在线推送
                Map<String, String> map = jedis.hgetAll(CodeUtil.REDIS_USER_ONLINE + to + CodeUtil.REDIS_USER_SUFFIX);
                if (map.isEmpty() && offLine == 1) { //用户不在线  消息支持离线推送
                    offlinePush(externalpush, jedis, to);  //离线推送
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(MessageCode.EXTERNALPUSH_ERR_PUSH);
        } finally {
            jedis.close();
        }
    }

    //离线推送
    private void offlinePush(ExternalPush externalpush, Jedis jedis, String to) {
        String pushKey = CodeUtil.REDIS_USER_PUSH + to + CodeUtil.REDIS_USER_SUFFIX;
        jedis.hincrBy(pushKey, CodeUtil.REDIS_USER_BADGE, 1);
        String badge = jedis.hget(pushKey, CodeUtil.REDIS_USER_BADGE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pushkey", Arrays.asList(to));
        jsonObject.put("title", externalpush.getTitle());
        jsonObject.put("content", externalpush.getText());
        jsonObject.put("sound", "default");
        jsonObject.put(CodeUtil.REDIS_USER_BADGE, badge);
        jsonObject.put("userdata", externalpush.getContent());
        String upload_url = env.getProperty(CodeUtil.CERT_PROD_UPLOAD_URL);  //地址
        String offline_push_url = env.getProperty(CodeUtil.OFFLINE_PUSH_URL);
        ResultData resultData = HttpClientUtil.httpRequest(upload_url + offline_push_url, CodeUtil.METHOD_POST,
                CodeUtil.CONTEXT_JSON, jsonObject.toString());
        if (resultData != null && CodeUtil.HTTP_OK != resultData.getCode()) {
            throw new ValueRuntimeException(MessageCode.MESSAGE_ERR_OFFLINE_PUSH);  //离线消息发送失败
        }
    }

    //在线推送
    private void onlinePush(ExternalPush externalpush, Jedis jedis, String to) throws UnsupportedEncodingException {
        byte[] cByte = CodeUtil.MESSAGE_EVENT_TYPE_EXTERNALPUSH.getBytes(CodeUtil.cs);
        byte[] uByte = to.getBytes(CodeUtil.cs);
        byte[] eIdByte = externalpush.getUuid().getBytes(CodeUtil.cs);
        byte[] pushByte = "0".getBytes(CodeUtil.cs);
        StringBuffer contentBuf = new StringBuffer();
        contentBuf.append(externalpush.getUuid())
                .append(":").append(RedisUtils.toBase64String("0"))
                .append(":").append(RedisUtils.toBase64String(externalpush.getContent()));
        byte[] dByte = contentBuf.toString().getBytes(CodeUtil.cs);
        jedis.publish("notice".getBytes(), RedisUtils.buildBytesArray(uByte, cByte, eIdByte, pushByte, dByte));
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
        resultMap.put("content", externalpush.getContent());
        List<ExternalTo> externalTos = externalToMapper.getByUuidAndTo(externalpush.getUuid(), by);
        resultMap.put("externalTos", externalTos);
        response.retDatas(resultMap);
        return response;
    }
}
