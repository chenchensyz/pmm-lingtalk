package cn.com.cybertech.tools;

public class MessageCode {

    public static final int BASE_SUCC_CODE = 0;                             // 请求成功
    public static final int BASE_PARAMS_ERR_VALIDE = 911001;                // 请填写完整参数

    //请求验证
    public static final int AUTH_HEADER_USER_NULL = 90101; //头信息中缺少 用户 信息
    public static final int AUTH_HEADER_TOKEN_NULL = 90102; //头信息中缺少 token 信息
    public static final int AUTH_HEADER_PLATFORM_NULL = 90103; //头信息中缺少 平台 信息
    public static final int AUTH_HEADER_TOKEN_FILED = 90104; //token验证失败
    public static final int AUTH_HEADER_USER_FILED = 90105; //用户验证失败

    //应用
    public static final int APPINFO_ERR_SELECT = 10101; //未查到应用
    public static final int APPINFO_ERR_OPERATION = 10102; //添加或修改应用失败
    public static final int APPINFO_ERR_UNENABLE = 10103; //应用未通过审核
    //用户
    public static final int USERINFO_ERR_SELECT = 20101; //用户不存在
    public static final int USERINFO_DISABLE = 20102; //用户已被禁用
    public static final int USERINFO_ERR_PASSWORD = 20103; //密码错误
    public static final int USERINFO_ERR_LOGIN = 20104; //用户登陆失败
    public static final int USERINFO_ERR_ADD = 20105; //添加或编辑用户失败
    public static final int USERINFO_ERR_DEL = 20106; //删除用户失败
    public static final int USERINFO_PASSWORD_NULL = 20107; //请输入密码
    public static final int USERINFO_EXIST = 20108; //用户已存在
    public static final int USERINFO_PARAM_NULL = 20109; //请填写完整登录信息

    public static final int ROLE_ERR_ADD = 30101; //角色增加失败
    public static final int ROLE_ERR_UPDATE = 30102; //角色修改失败
    public static final int ROLE_PERM_ERR_ADD = 30103; //角色权限增加失败
    public static final int ROLE_PERM_ERR_DEL = 30104; //角色权限重置失败

    //公司
    public static final int COMPANYINFO_ERR_SELECT = 40101; //未查到公司
    public static final int COMPANYINFO_ERR_OPERATION = 40102; //添加或修改公司失败
    public static final int COMPANYINFO_ERR_DELECT = 40103; //删除公司失败
}
