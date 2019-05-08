package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SkuPropertyDetail implements Serializable {
    private String propertyName;

    private List<String> optionList;
}
