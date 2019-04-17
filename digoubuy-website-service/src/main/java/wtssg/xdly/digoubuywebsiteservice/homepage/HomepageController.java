package wtssg.xdly.digoubuywebsiteservice.homepage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("homepage")
public class HomepageController {
    @RequestMapping
    public String homepage() {
        return "homepage";
    }
}
