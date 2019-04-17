package wtssg.xdly.digoubuytradeservice.trade.handle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class OrderKeyExpiredHandler {

    @Bean
    public RedisMessageListenerContainer configRedidMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(redisConnectionFactory);
        listenerContainer.addMessageListener((message, listener)->{
            // 处理key过期事件的逻辑
            System.out.println("---redis过期事件" + new String(message.getBody()));
        }, new PatternTopic("__keyevent@0__:expired"));
        return listenerContainer;
    }
}
