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

    public static final int HTTP_OK = 200;
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_DELETE = "DELETE";

    public static final String USER_TYPE_SYS = "sys";
    public static final String USER_TYPE_WEB = "web";

    public static final int REDIS_DBINDEX = 2;    //token报存在redis第二个库
    public static final String REDIS_PREFIX = "session:";    //token保存在redis第二个库
    public static final String REDIS_APPLOGIN_PREFIX = "as:";   //IM用户登陆

    public static final int ROLE_COMPANY_MANAGER = 3;   //角色 公司管理员
    public static final int ROLE_COMPANY_DEVELOPER = 4;   //角色 公司开发者

    //证书
    public static final String CERT_IOS = "apple";
    public static final String CERT_HUAWEI = "huawei";
    public static final String CERT_XIAOMI = "mi";
    public static final String CERT_OPPO = "oppo";
    public static final String CERT_VIVO= "vivo";
    public static final String CERT_SAVE_PATH = "cert_save_path";
    public static final String CERT_UPLOAD_URL = "cert_upload_url";  //测试环境地址
    public static final String CERT_PROD_UPLOAD_URL = "cert_prod_upload_url"; //生产环境地址
    public static final String CERT_CHANGE_URL = "change_app";
    public static final String CERT_DELETE_URL = "delete_cert";
    public static final String PUSH_STATUS_URL = "push_status";
    public static final String CERT_TYPE_FILE = "file";
    public static final String CERT_TYPE_KEY = "key";

    //im用户
    public static final String DEFAULT_ROLE_ID = "RID_PRIVILEGE";
    public static final int PMUSER_STATE_ACTIVATED  = 1;

    //http
    public static final String REQUEST_MAXTIME = "request_maxtime";
}
