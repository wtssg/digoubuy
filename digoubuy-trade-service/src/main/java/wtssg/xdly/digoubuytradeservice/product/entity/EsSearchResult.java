package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.util.List;


@Data
public class EsSearchResult {
    private int total;

    private List<Product> productList;

}
