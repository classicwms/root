package com.tekclover.wms.api.transaction.model.inbound.gr.v2;

import com.tekclover.wms.api.transaction.model.inbound.gr.AddGrLine;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AddGrLineV2 extends AddGrLine {

    private Double inventoryQuantity;
    private String barcodeId;
    //    private Double cbm;
    private String cbmUnit;
    private String manufacturerCode;
    private String manufacturerName;
    private String storageSectionId;
    private String origin;
    private String brand;
    private String rejectType;
    private String rejectReason;
    //    private Double cbmQuantity;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String interimStorageBin;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private String middlewareId;
    private String middlewareHeaderId;
    private String middlewareTable;
    private String purchaseOrderNumber;
    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
	private String statusDescription;
	private String putAwayNumber;
	private String parentProductionOrderNo;
}