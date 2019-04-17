package wtssg.xdly.digoubuyuserservice.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@EnableBinding(Source.class)
@Service("KafkaSendService")
public class KafkaSendService {
    @Autowired
    private Source source;

    public void sendSms(String sms) {
        source.output().send(MessageBuilder.withPayload(sms).build());
    }
}
