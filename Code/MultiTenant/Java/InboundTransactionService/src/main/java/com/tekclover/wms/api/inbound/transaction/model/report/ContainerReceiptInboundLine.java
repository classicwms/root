package com.tekclover.wms.api.inbound.transaction.model.report;

import lombok.Data;

import java.util.Date;

@Data
public class ContainerReceiptInboundLine {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preInboundNo;
    private String refDocNumber;
    private String containerReceiptNo;
    private Date containerReceivedDate;
    private String containerNo;
    private Long statusId;
    private String containerType;
    private String partnerCode;
    private String invoiceNo;
    private String consignmentType;
    private String origin;
    private String numberOfPallets;
    private String numberOfCases;
    private String dockAllocationNo;
    private String remarks;
    private Long deletionIndicator;
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

    private String ibReferenceField1;
    private String ibReferenceField2;
    private String ibReferenceField3;
    private String ibReferenceField4;
    private String ibReferenceField5;
    private String ibReferenceField6;
    private String ibReferenceField7;
    private String ibReferenceField8;
    private String ibReferenceField9;
    private String ibReferenceField10;
    private String ibstatusDescription;
    private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
    private Date updatedOn = new Date();
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String purchaseOrderNumber;
    private String middlewareId;
    private String middlewareTable;
    private Date referenceField11;
    private Date referenceField12;
    private Date referenceField13;
    private Date referenceField14;
    private Date referenceField15;
    private Date referenceField16;
    private Date referenceField17;
    private Date referenceField18;
    private Date referenceField19;
    private Date referenceField20;
    private String referenceField30;
    private String referenceField27;



    //InboundLine

    private Long lineNo;
    private String itemCode;
    private Double orderQty;
    private String orderUom;
    private Double acceptedQty;
    private Double damageQty;
    private Double putawayConfirmedQty;
    private Double varianceQty;
    private Long variantCode;
    private Long inboundOrderTypeId;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String referenceOrderNo;
    private String vendorCode;
    private Date expectedArrivalDate;
    private String description;
    private String manufacturerPartNo;
    private String hsnCode;
    private String itemBarcode;
    private Double itemCaseQty; // PACK_QTY in AX_API
    private String confirmedBy;
    private Date confirmedOn;
    private String manufacturerCode;
    private String manufacturerName;
    private String storageSectionId;
    private String middlewareHeaderId;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private String parentProductionOrderNo;
    private String supplierName;
    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private String sourceBranchCode;
    private String sourceCompanyCode;
    private String AMSSupplierInvoiceNo;
    private String batchSerialNumber;
    private Date manufacturerDate;
    private Date expiryDate;
    private String barcodeId;
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;
    private String brand;
    private Double actualAcceptedQty;
    private Double actualDamageQty;
    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCreate;
    private String vehicleNo;
    private Date vehicleReportingDate;
    private Date vehicleUnloadingDate;
    private String uomQty;

    private String inventoryOwner;
    private String referenceField25;

}
