package wtssg.xdly.digoubuytradeservice.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import wtssg.xdly.digoubuytradeservice.common.constants.Constants;
import wtssg.xdly.digoubuytradeservice.common.resp.ApiResult;
import wtssg.xdly.digoubuytradeservice.product.entity.*;
import wtssg.xdly.digoubuytradeservice.product.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {
    @Autowired
    @Qualifier("productServiceImpl")
    private ProductService productService;


    @RequestMapping("/search")
    public ApiResult<EsSearchResult> searchProduct(@RequestBody PageSearch pageSearch) throws IOException {

        ApiResult<EsSearchResult> result = new ApiResult<>(Constants.RESP_STATUS_OK, "搜索产品成功");
        EsSearchResult esSearchResult = productService.searchProduct(pageSearch.getPageNumber(),
                pageSearch.getPageSize(), pageSearch.getSortCondition(),
                pageSearch.getSearchContent());
        result.setData(esSearchResult);
        return result;
    }

    @RequestMapping("/searchByCategory/{categoryId}")
    public ApiResult<EsSearchResult> searchByCategory(@PathVariable("categoryId") int categoryId) throws IOException {
        ApiResult<EsSearchResult> result = new ApiResult<>(Constants.RESP_STATUS_OK, "查询分类产品成功");
        EsSearchResult esSearchResult = productService.searchByCategory(categoryId);
        result.setData(esSearchResult);
        return result;
    }

    @RequestMapping("/category")
    public ApiResult<List<Category>> listCategory() {
        ApiResult<List<Category>> result = new ApiResult<>(Constants.RESP_STATUS_OK, "查询分类成功");
        List<Category> list = productService.listCategory();
        result.setData(list);
        return result;
    }

    @RequestMapping("/productDetail/{productId}")
    public ApiResult<PropertyOptionAndProductDetail> productDetail(@PathVariable("productId") int productId) {
        ApiResult<PropertyOptionAndProductDetail> result = new ApiResult<>(Constants.RESP_STATUS_OK, "查询产品成功");
        PropertyOptionAndProductDetail data = productService.productDetail(productId);
        result.setData(data);
        return result;
    }

}
