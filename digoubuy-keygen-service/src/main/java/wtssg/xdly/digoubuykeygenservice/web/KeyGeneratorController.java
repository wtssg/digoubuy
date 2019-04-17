package wtssg.xdly.digoubuykeygenservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wtssg.xdly.digoubuykeygenservice.keygen.SnowFlakeKeyGenerator;

@RestController
@RequestMapping
public class KeyGeneratorController {

    @Autowired
    @Qualifier("snowFlakeKeyGenerator")
    private SnowFlakeKeyGenerator KeyGenerator;

    @RequestMapping("/keygen")
    public String keyGenerator() throws Exception {
        return String.valueOf(KeyGenerator.generateKey().longValue());
    }
}
