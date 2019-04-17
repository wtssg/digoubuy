package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class Category implements Serializable {

    private Long id;

    private Long parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private List<Category> children;


    private transient Date createTime;

    private transient Date updateTime;

}