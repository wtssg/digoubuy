package wtssg.xdly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import java.util.ArrayList;
import java.util.LinkedList;

@SpringBootApplication
@EnableEurekaServer
public class DigouBuyServiceRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DigouBuyServiceRegistryApplication.class, args);
    }
}
