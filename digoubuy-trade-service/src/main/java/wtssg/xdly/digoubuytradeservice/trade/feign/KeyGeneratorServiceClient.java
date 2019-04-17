package wtssg.xdly.digoubuytradeservice.trade.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "key-generator", fallback = KeyGeneratorServiceFallBack.class)
public interface KeyGeneratorServiceClient {

    @RequestMapping("/keygen")
    String keyGenerator();
}
