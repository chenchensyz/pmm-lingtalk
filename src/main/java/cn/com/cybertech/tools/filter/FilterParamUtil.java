package cn.com.cybertech.tools.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

public class FilterParamUtil {

    public static final String APPCONFIG_FILTER = "id,appId";
    public static final String COMMON_FILTER = "id,platform,version,state,createUser,createTime," +
            "updateUser,updateTime,deleted,description,deleteTime,pageNum,pageSize";
    public static final String EXTERNALTO_FILTER = "acktime,expire,push.to,push.push.appId";
    public static final String EXTERNALTOS_PUSH_FILTER = "uuid,acktime,expire,offLine";

    public static Object filterParam(Object o, String filters) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        Set<String> filterSet = Sets.newHashSet(Arrays.asList(filters.split(",")));
        filter.getExcludes().addAll(filterSet);
        String jsonString = JSONObject.toJSONString(o, filter);
        return JSONObject.parse(jsonString);
    }
}
