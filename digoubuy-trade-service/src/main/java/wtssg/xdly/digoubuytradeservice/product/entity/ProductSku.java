package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ProductSku implements Serializable {

    private Long id;

    private Long spuId;

    private String skuName;

    private BigDecimal skuPrice;

    private String imgUrl;

    private Byte enableFlag;

    private List<SkuPropertyOption> skuPropertyOptionList;

    private transient Date createTime;

    private transient Date updateTime;

}