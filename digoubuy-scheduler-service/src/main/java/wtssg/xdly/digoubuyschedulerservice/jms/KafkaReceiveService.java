package wtssg.xdly.digoubuyschedulerservice.jms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.data.redis.core.RedisTemplate;
import wtssg.xdly.digoubuyschedulerservice.common.constants.Constants;
import wtssg.xdly.digoubuyschedulerservice.sms.SmsSender;

import java.util.concurrent.TimeUnit;

@EnableBinding(Sink.class)
public class KafkaReceiveService {

    @Autowired
    private SmsSender smsSender;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @StreamListener(Sink.INPUT)
    public void receive(String payload) {
        System.out.println(payload + "hello");
        JSONObject jsonObject = JSON.parseObject(payload);
        System.out.println("hello");
        smsSender.sendSms(jsonObject.getString("mobile"), jsonObject.getString("tplId"), jsonObject.getString("verCode"));
    }
}
