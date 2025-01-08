package com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2;

import com.tekclover.wms.api.transaction.model.outbound.preoutbound.OutboundIntegrationLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class OutboundIntegrationLineV2 extends OutboundIntegrationLine {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    //---------------------ALM Changes-----------------------------------------
    private String manufacturerCode;                        // MFR_CODE
    private String manufacturerName;                        // MFR_CODE
    private String storageSectionId;
    private String origin;                                    // ORIGIN
    private String brand;                                    // BRAND
    private String companyCode;
    private String branchCode;

    private String salesOrderNumber;
    private String pickListNo;
    private String manufacturerFullName;
    private Long middlewareId;
    private Long middlewareHeaderId;
    private String middlewareTable;
    private String salesInvoiceNo;
    private String supplierInvoiceNo;
    private String referenceDocumentType;

    private String transferOrderNo;
    private String returnOrderNo;
    private String isCompleted;
    private String isCancelled;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
	private String barcodeId;

    //==========================Impex==============================================//
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;

}