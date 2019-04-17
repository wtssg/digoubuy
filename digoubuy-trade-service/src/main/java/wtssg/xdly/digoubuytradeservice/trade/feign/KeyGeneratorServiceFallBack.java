package wtssg.xdly.digoubuytradeservice.trade.feign;

public class KeyGeneratorServiceFallBack implements KeyGeneratorServiceClient {
    @Override
    public String keyGenerator() {
        return null;
    }
}
