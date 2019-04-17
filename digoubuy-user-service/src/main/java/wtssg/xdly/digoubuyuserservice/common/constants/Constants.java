package wtssg.xdly.digoubuyuserservice.common.constants;

/**
 * Created by JackWangon[www.coder520.com] 2018/1/9.
 */
public class Constants {
    /**自定义状态码 start**/
    public static final int RESP_STATUS_OK = 200;

    public static final int RESP_STATUS_NOAUTH = 401;

    public static final int RESP_STATUS_INTERNAL_ERROR = 500;

    public static final int RESP_STATUS_BADREQUEST = 400;
    /**自定义状态码 end**/

    /**用户token**/
    public static final String REQUEST_TOKEN_HEADER = "x-auth-token";
    /**用户session***/
    public static final String REQUEST_USER_SESSION = "current-user";
    /**客户端版本**/
    public static final String REQUEST_VERSION_KEY = "version";


    /**用户注册分布式锁路径***/
    public static final String USER_REGISTER_DISTRIBUTE_LOCK_PATH = "/user_reg";


    /**
     * 秒滴短信验证码
     */
    public static final String MDSMS_ACCOUNT_SID = "719a85d39d9c49d7ac447468941d7d62";

    public static final String MDSMS_AUTH_TOKEN = "afc000de45734280aec6c9a38927f502";

    public static final String MDSMS_REST_URL = "https://api.miaodiyun.com/20150822";

    public static final String MDSMS_VERCODE_TPLID = "751915103";


}
