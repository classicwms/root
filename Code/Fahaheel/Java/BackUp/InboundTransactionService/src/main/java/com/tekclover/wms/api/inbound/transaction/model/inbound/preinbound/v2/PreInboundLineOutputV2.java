package com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PreInboundLineOutputV2 {

    private String languageId;
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String preInboundNo;
    private String refDocNumber;
    private Long lineNo;
    private String itemCode;
    private Long inboundOrderTypeId;
    private Long variantCode;
    private String variantSubCode;
    private Long statusId;
    private String itemDescription;
    private String containerNo;
    private String invoiceNo;
    private String businessPartnerCode;
    private String partnerItemNo;
    private String brandName;
    private String manufacturerPartNo;
    private String hsnCode;
    private Date expectedArrivalDate;
    private Double orderQty;
    private String orderUom;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String numberOfPallets;
    private String numberOfCases;
    private Double itemPerPalletQty;
    private Double itemCaseQty; // PACK_QTY in AX_API
    private String referenceField1;
    private String referenceField2;
    private String referenceField3;
    private String referenceField4;
    private String referenceField5;
    private String referenceField6;
    private String referenceField7;
    private String referenceField8;
    private String referenceField9;
    private String referenceField10;
    private String referenceField11;
    private String referenceField12;
    private String referenceField13;
    private String referenceField14;
    private String referenceField15;
    private String referenceField16;
    private String referenceField17;
    private String referenceField18;
    private String referenceField19;
    private String referenceField20;
    private Long deletionIndicator;
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    private String manufacturerCode;
    private String manufacturerName;
    private String storageSectionId;
    private String origin;
    private String supplierName;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String middlewareId;
    private String middlewareHeaderId;
    private String middlewareTable;
    private String referenceDocumentType;
    private String purchaseOrderNumber;
    private String manufacturerFullName;
    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;

    private List<InventoryDetail> inventoryDetail;

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
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private String stockTypeDescription;
}