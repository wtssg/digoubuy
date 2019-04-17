package wtssg.xdly.digoubuywebsiteservice.product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("product")
public class ProductController {

    @RequestMapping("/search")
    public String Search() {
        return "search";
    }

    @RequestMapping("/productDetail")
    public String productDetail() {
        return "product_detail";
    }

    @RequestMapping("/temp")
    public String Temp() {
        return "temp";
    }
}
