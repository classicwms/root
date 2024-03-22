package com.tekclover.wms.api.transaction.model.warehouse.inbound.v2;

import com.tekclover.wms.api.transaction.model.warehouse.inbound.InboundOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class InboundOrderV2 extends InboundOrder {

    private String branchCode;
    private String companyCode;
    private String returnOrderReference;

    private String transferOrderNumber;

    private String sourceCompanyCode;
    private String sourceBranchCode;
    private Date transferOrderDate;
    private String isCompleted;
    private Date updatedOn;
    private String isCancelled;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inboundOrderHeaderId", fetch = FetchType.EAGER)
    private Set<InboundOrderLinesV2> line;

}