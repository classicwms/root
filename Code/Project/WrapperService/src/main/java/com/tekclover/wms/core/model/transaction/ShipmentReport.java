package com.tekclover.wms.core.model.transaction;


import lombok.Data;

import java.util.Date;

@Data
public class ShipmentReport {

    private Long referenceId;

    private String loginUserId;

    private String warehouseId;

    private Date fromDate;

    private Date toDate;

    private Long statusId;
}