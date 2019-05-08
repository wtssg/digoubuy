package wtssg.xdly.digoubuystockservice.common.constants;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 系统参数
 */
@Component
@Data
public class Parameters {

    /***zk config start ***/
    @Value("${kafka.node}")
    private String kafkaHost;


}
