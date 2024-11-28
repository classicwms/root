package com.tekclover.wms.api.transaction.model.warehouse.outbound.v2;

import com.tekclover.wms.api.transaction.model.warehouse.outbound.OutboundOrderLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@ToString(callSuper = true)
public class OutboundOrderLineV2 extends OutboundOrderLine {

    private String manufacturerCode;
    private String origin;
    private String supplierName;
    private String brand;
    private Double packQty;
    private String fromCompanyCode;
    private Double expectedQty;
    protected String storeID;

    private String sourceBranchCode;
    private String countryOfOrigin;

    private String manufacturerName;
    private String manufacturerFullName;
    private String fulfilmentMethod;

    private String storageSectionId;

    private String salesOrderNo;
    private String pickListNo;

    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;
    private String transferOrderNumber;
    private String salesInvoiceNo;
    private String supplierInvoiceNo;
    private String returnOrderNo;
    private String isCompleted;
    private String isCancelled;
    private String customerType;
    private Long outboundOrderTypeID;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
	private String barcodeId;
    /*----------------------Impex--------------------------------------------------*/
    @Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
    private String alternateUom;

    @Column(name = "NO_BAGS")
    private Double noBags;

    @Column(name = "BAG_SIZE")
    private Double bagSize;

    @Column(name = "MRP")
    private Double mrp;
}