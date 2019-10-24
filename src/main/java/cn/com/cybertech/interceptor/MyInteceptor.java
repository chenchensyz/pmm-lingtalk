package cn.com.cybertech.interceptor;

import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.MessageCode;
import cn.com.cybertech.tools.RestResponse;
import cn.com.cybertech.tools.SpringUtil;
import cn.com.cybertech.tools.exception.ValueRuntimeException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Configuration
public class MyInteceptor implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //web后台接口拦截器
        String webExcludePath = env.getProperty("interceptor.webExcludePath");
        String[] webExcludeArr = webExcludePath.split(",");
        registry.addInterceptor(new AuthCheckInterceptor())
                .addPathPatterns("/**").excludePathPatterns(webExcludeArr);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addRedirectViewController("/admin", "/static/index.html");
//    }

}

/**
 * 微服务间接口访问密钥验证
 *
 * @author xiaochangwei
 */
class AuthCheckInterceptor implements HandlerInterceptor {

    private static Map<Integer, String> messageMap = Maps.newHashMap();

    static {
        messageMap.put(MessageCode.AUTH_HEADER_USER_NULL, "头信息中缺少 用户 信息");
        messageMap.put(MessageCode.AUTH_HEADER_TOKEN_NULL, "头信息中缺少 token 信息");
        messageMap.put(MessageCode.AUTH_HEADER_PLATFORM_NULL, "头信息中缺少 平台 信息");
        messageMap.put(MessageCode.AUTH_HEADER_TOKEN_FILED, "token验证失败");
        messageMap.put(MessageCode.AUTH_HEADER_USER_FILED, "用户验证失败");
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MyInteceptor.class);

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        String platform = request.getHeader("platform");
        String userId = request.getHeader("userId");
        if (StringUtils.isBlank(token)) {
            writeJsonResult(response, 401, MessageCode.AUTH_HEADER_TOKEN_NULL, messageMap.get(MessageCode.AUTH_HEADER_TOKEN_NULL));
            return false;
        }
        if (StringUtils.isBlank(platform)) {
            writeJsonResult(response, 401, MessageCode.AUTH_HEADER_PLATFORM_NULL, messageMap.get(MessageCode.AUTH_HEADER_PLATFORM_NULL));
            return false;
        }
        if (StringUtils.isBlank(userId)) {
            writeJsonResult(response, 401, MessageCode.AUTH_HEADER_USER_NULL, messageMap.get(MessageCode.AUTH_HEADER_USER_NULL));
            return false;
        }

        JedisPool jedisPool = SpringUtil.getBean(JedisPool.class);
        Jedis jedis = jedisPool.getResource();
        jedis.select(CodeUtil.REDIS_DBINDEX);
        try {
            Map<String, String> tokenMap = jedis.hgetAll(CodeUtil.REDIS_PREFIX + token);
            if (tokenMap.isEmpty()) {
                writeJsonResult(response, 401, MessageCode.AUTH_HEADER_TOKEN_FILED, messageMap.get(MessageCode.AUTH_HEADER_PLATFORM_NULL));
                return false;
            }
            if (!userId.equals(tokenMap.get("userId"))) {
                writeJsonResult(response, 401, MessageCode.AUTH_HEADER_USER_FILED, messageMap.get(MessageCode.AUTH_HEADER_PLATFORM_NULL));
                return false;
            }
            jedis.expire(CodeUtil.REDIS_PREFIX + token, CodeUtil.REDIS_EXPIRE_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValueRuntimeException(MessageCode.USERINFO_ERR_LOGIN); //用户登陆失败
        } finally {
            jedis.close();
        }
        return true;
    }

    private void writeJsonResult(HttpServletResponse response, int httpCode, int code, String msg) {
        response.setStatus(httpCode);
        response.setContentType(CodeUtil.CONTEXT_JSON);
        response.setCharacterEncoding(CodeUtil.cs.toString());
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.print(RestResponse.res(code, msg));
        out.flush();
    }
}