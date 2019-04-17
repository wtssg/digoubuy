package wtssg.xdly.digoubuytradeservice.product.entity;

import lombok.Data;

@Data
public class PageSearch {

    private int pageNumber;

    private int pageSize;

    private int sortCondition;

    private String searchContent;

}
