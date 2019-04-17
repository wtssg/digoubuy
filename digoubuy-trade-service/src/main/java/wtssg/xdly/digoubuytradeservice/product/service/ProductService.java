package wtssg.xdly.digoubuytradeservice.product.service;

import wtssg.xdly.digoubuytradeservice.product.entity.Category;
import wtssg.xdly.digoubuytradeservice.product.entity.EsSearchResult;
import wtssg.xdly.digoubuytradeservice.product.entity.Product;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    EsSearchResult searchProduct(int pageNumber, int pageSize, int searchCondition, String searchContent) throws IOException;

    EsSearchResult searchByCategory(int categoryId) throws IOException;

    List<Category> listCategory();

    Product productDetail(int productId);
}
