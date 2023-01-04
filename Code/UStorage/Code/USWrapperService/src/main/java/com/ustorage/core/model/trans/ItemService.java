package com.ustorage.core.model.trans;

import lombok.Data;

import java.util.Date;
@Data
public class ItemService {

    //private long id;
    private String workOrderId;
    private String itemName;
    private String itemServiceName;
    private Double itemServiceQuantity;
    private Double itemServiceUnitPrice;
    private Double itemServiceTotal;
    private Long deletionIndicator = 0L;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;

}
