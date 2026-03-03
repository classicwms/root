package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class StagingLineNewReport{

    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String preInboundNo;

    private String refDocNumber;

    private String stagingNo;

    private String palletCode;

    private String caseCode;

    private Long lineNo;

    private String itemCode;

    private Long inboundOrderTypeId;

    private Long variantCode;

    private String variantSubCode;

    private String batchSerialNumber;

    private Long stockTypeId;

    private Long specialStockIndicatorId;

    private String storageMethod;

    private Long statusId;

    private String businessPartnerCode;

    private String containerNo;

    private String invoiceNo;

    private Double orderQty;

    private String orderUom;

    private Double itemQtyPerPallet;

    private Double itemQtyPerCase;

    private String itemDescription;

    private String manufacturerPartNo;

    private String hsnCode;

    private String variantType;

    private String specificationActual;

    private String itemBarcode;

    private String referenceOrderNo;

    private Double referenceOrderQty;

    private String remarks;

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

    private Long deletionIndicator;

    private String createdBy;

    private Date createdOn;

    private String updatedBy;

    private Date updatedOn;

    private String confirmedBy;

    private Date confirmedOn;

    private String companyDescription;

    private String plantDescription;

    private String warehouseDescription;

    private String statusDescription;

    private Double inventoryQuantity;

    private String manufacturerCode;

    private String manufacturerName;

    private String storageSectionId;

    private String origin;

    private String brand;

    private String barcodeId;

    private Double rec_accept_qty;

    private Double rec_damage_qty;

    private String middlewareId;

    private String middlewareHeaderId;

    private String middlewareTable;

    private String purchaseOrderNumber;

    private String parentProductionOrderNo;

    private String manufacturerFullName;

    private String referenceDocumentType;

    private String branchCode;

    private String transferOrderNo;

    private String isCompleted;

    private String materialNo;

    private String priceSegment;

    private String articleNo;

    private String gender;

    private String color;

    private String size;

    private String noPairs;

    private Long totalCount;

    private Long totalBarcodeStatusCount;

    private String vehicleNumber;

    private Long vehicleTotalHuNumber;

    private Long vehicleScanHuNumber;

    private String palletId;

    private String grLineFlag;

    private String sapFlag;

    private String sapType;

    private String sapMessage;

    private String sapDocumentNo;

    private Date materialDocDate;

    private String mtoNumber;

    private Double putAwayQuantity;

    private Date putAwayDate;

    private String putAwayRefDocNo;

    private String putAwayBarcodeId;

    private String putAwayItemCode;

    private String putAwayHeaderStatus;


}
