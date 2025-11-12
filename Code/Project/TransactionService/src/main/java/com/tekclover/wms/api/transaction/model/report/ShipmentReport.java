package com.tekclover.wms.api.transaction.model.report;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "tblshipmentreport")
public class ShipmentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long referenceId;

    private String loginUserId;

    private String warehouseId;

    private Date fromDate;

    private Date toDate;

    private Long statusId;
}
