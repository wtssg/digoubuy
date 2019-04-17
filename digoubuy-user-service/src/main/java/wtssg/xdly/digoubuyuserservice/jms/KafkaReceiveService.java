package wtssg.xdly.digoubuyuserservice.jms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import wtssg.xdly.digoubuyuserservice.sms.SmsSender;

@EnableBinding(Sink.class)
public class KafkaReceiveService {

    @Autowired
    private SmsSender smsSender;

    @StreamListener(Sink.INPUT)
    public void receive(String payload) {
        JSONObject jsonObject = JSON.parseObject(payload);
        System.out.println("hello");
        smsSender.sendSms(jsonObject.getString("mobile"), jsonObject.getString("tplId"), jsonObject.getString("verCode"));
    }
}
