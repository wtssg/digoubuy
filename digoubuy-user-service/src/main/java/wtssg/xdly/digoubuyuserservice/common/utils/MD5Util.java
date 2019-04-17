package wtssg.xdly.digoubuyuserservice.common.utils;/**
 * Created by wangjianbin on 2017/8/1.
 */


import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5工具类
 **/
public class MD5Util {


    public static String getMD5(String source){
        return DigestUtils.md5Hex(source);
    }

}
