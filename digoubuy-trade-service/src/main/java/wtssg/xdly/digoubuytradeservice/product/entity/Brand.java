package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Brand {

    private Long id;

    private String brandName;

    private Date createTime;

    private Date updateTime;

}