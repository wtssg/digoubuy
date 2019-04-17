package wtssg.xdly.digoubuyuserservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import wtssg.xdly.digoubuyuserservice.common.constants.Parameters;

//@Configuration
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 60)
//public class RedisHttpSessionConfig {


//    @Autowired
//    Parameters parameters;
//
//
//    @Bean
//    public JedisConnectionFactory connectionFactory() {
//
//        String redisHost = parameters.getRedisNode().split(":")[0];
//        int redisPort = Integer.parseInt(parameters.getRedisNode().split(":")[1]);
//        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
//        return new JedisConnectionFactory(standaloneConfig);
//    }
//    @Bean
//    public HttpSessionIdResolver httpSessionIdResolver() {
//        return HeaderHttpSessionIdResolver.xAuthToken();
//    }
//
//    @Bean
//    public static ConfigureRedisAction configureRedisAction() {
//        return ConfigureRedisAction.NO_OP;
//    }
//}

