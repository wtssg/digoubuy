package wtssg.xdly.digoubuywebsiteservice.trade;

import com.sun.deploy.net.HttpResponse;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("trade")
public class TradeController {

    @RequestMapping("/cart")
    public String shoppingCart() {
        return "shopping-cart";
    }

    @RequestMapping("/tempOrder")
    public String tempOrder() {
        return "tempOrder";
    }

    @RequestMapping("/cartOrder")
    public String cartOrder() {
        return "cartOrder";
    }

    @RequestMapping("/cashRegister")
    public String cashRegister() {
        return "cashRegister";
    }



}
