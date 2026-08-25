package com.tekclover.wms.api.inbound.orders.model.warehouse.cyclecount;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name = "tblcyclecountheader")
public class CycleCountHeader {

    @Id
    @Column(name = "order_id",nullable = false)
    private String orderId;

    private String companyCode;
    private String cycleCountNo;
    private String branchCode;
    private String branchName;
    private Date cycleCountCreationDate;
    private String isNew;
    private Date orderProcessedOn;
    private Date orderReceivedOn;
    private Long processedStatusId;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
    private String isCompleted;
    private String isCancelled;
    private String stockCountType;
    private Date updatedOn;


    @OneToMany(cascade = CascadeType.ALL,mappedBy = "orderId",fetch = FetchType.EAGER)
    private Set<CycleCountLine> lines;

}
