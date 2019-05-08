package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class Product implements Serializable {
    private Long id;

    private Long categoryId;

    private Long brandId;

    private String spuName;

    private String titleName;

    private BigDecimal titlePrice;

    private String titleImgUrl;

    private String featureImgUrl;

    private Byte status;

    private Date createTime;

    private Date updateTime;

    private List<ProductSku> skuList;

}