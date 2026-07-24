package com.tekclover.wms.api.inbound.transaction.model.v2;

import com.tekclover.wms.api.inbound.transaction.model.preoutbound.v2.OutboundOrderLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    private String shipToCode;
    private String shipToParty;
    private String specialStock;
    private String mtoNumber;
}
