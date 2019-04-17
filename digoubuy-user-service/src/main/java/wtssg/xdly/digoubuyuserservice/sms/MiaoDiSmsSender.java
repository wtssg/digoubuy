package wtssg.xdly.digoubuyuserservice.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import wtssg.xdly.digoubuyuserservice.common.constants.Constants;
import wtssg.xdly.digoubuyuserservice.common.utils.HttpUtil;
import wtssg.xdly.digoubuyuserservice.common.utils.MD5Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("verCodeService")
@Slf4j
public class MiaoDiSmsSender implements SmsSender {
    private static String operation = "/industrySMS/sendSMS";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public  void sendSms(String phone,String tplId,String params){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = sdf.format(new Date());
            String sig = MD5Util.getMD5(Constants.MDSMS_ACCOUNT_SID +Constants.MDSMS_AUTH_TOKEN +timestamp);
            String url = Constants.MDSMS_REST_URL +operation;
            Map<String,String> param = new HashMap<>();
            param.put("accountSid",Constants.MDSMS_ACCOUNT_SID);
            param.put("to",phone);
            param.put("templateid",tplId);
            param.put("param",params);
            param.put("timestamp",timestamp);
            param.put("sig",sig);
            param.put("respDataType","json");
            String result = HttpUtil.post(url,param);
            JSONObject jsonObject = JSON.parseObject(result);
            if(!jsonObject.getString("respCode").equals("00000")){
                log.error("fail to send sms to "+phone+":"+params+":"+result);
            } else {
                redisTemplate.opsForValue().set(phone, params, 30, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("fail to send sms to "+phone+":"+params);
        }
    }

//    public static void main(String[] args) {
//        SmsSender smsSender = new MiaoDiSmsSender();
//        smsSender.sendSms("18838981005", Constants.MDSMS_VERCODE_TPLID, "1234");
//    }
}
