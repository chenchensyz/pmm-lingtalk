package cn.com.cybertech.tools;


import java.nio.charset.Charset;

public class CodeUtil {

    //code_info表类型
    public static final String CODE_METHOD = "method";
    public static final String CODE_CONTENTTYPE = "contentType";
    public static final String CODE_INSTRUCTIONSPATH = "instructionsPath";

    public static final String DEFAULT_PASSWORD = "888888";  //默认密码

    public static final Charset cs = Charset.forName("UTF-8");

    //content-type编码格式
    public static final String CONTEXT_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTEXT_FORM_DATA = "application/form_data";
    public static final String CONTEXT_JSON = "application/json";

    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";

    public static final int REDIS_DBINDEX = 2;   //token报存在redis第二个库
    public static final String REDIS_PREFIX = "session:";   //token报存在redis第二个库

    public static final int ROLE_COMPANY_MANAGER = 2;   //角色 公司管理员
    public static final int ROLE_COMPANY_DEVELOPER = 3;   //角色 公司开发者

    //证书类型
    public static final String CERT_IOS = "iOS";
    public static final String CERT_HUAWEI = "Huawei";
    public static final String CERT_XIAOMI = "Xiaomi";

}
