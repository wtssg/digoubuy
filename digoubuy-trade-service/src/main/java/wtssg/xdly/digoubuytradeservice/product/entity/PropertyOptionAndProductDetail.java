package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PropertyOptionAndProductDetail implements Serializable {
    private Product product;
    private List<SkuPropertyDetail> propertyOptionList;
}
