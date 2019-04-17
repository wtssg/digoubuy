package wtssg.xdly.digoubuyuserservice.common.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import wtssg.xdly.digoubuyuserservice.common.constants.Parameters;

@Component
public class ZkClient {
    @Autowired
    private Parameters parameters;

    @Bean
    public CuratorFramework getZkClient() {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(parameters.getZkHost())
                .connectionTimeoutMs(3000)
                .retryPolicy(new RetryNTimes(5, 500));
        CuratorFramework framework = builder.build();
        framework.start();
        return framework;
    }
}
