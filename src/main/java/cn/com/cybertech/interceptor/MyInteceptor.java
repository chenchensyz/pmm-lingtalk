package cn.com.cybertech.interceptor;

import cn.com.cybertech.tools.CodeUtil;
import cn.com.cybertech.tools.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class MyInteceptor implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //web后台接口拦截器
        String webExcludePath = env.getProperty("interceptor.webExcludePath");
        String[] webExcludeArr = webExcludePath.split(",");
        registry.addInterceptor(new AuthCheckInterceptor()).addPathPatterns("/**").excludePathPatterns(webExcludeArr);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        registry.addResourceHandler("/views/**").addResourceLocations("classpath:/views/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/index","/views/index.html");
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowCredentials(true).allowedHeaders("Origin, X-Requested-With, Content-Type, Accept")
//                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS");
//    }

}

/**
 * 微服务间接口访问密钥验证
 *
 * @author xiaochangwei
 */
class AuthCheckInterceptor implements HandlerInterceptor {

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
        LOGGER.info("token :{}", token);
        boolean isLogin = false;
        if (StringUtils.isNotBlank(token)) {
            isLogin = true;
        }
        if (!isLogin) {
            writeJsonResult(response, 401, 1, "未登录");
            return false;
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
        out.print(RestResponse.failure("未登录"));
        out.flush();
    }
}