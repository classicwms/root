package com.tekclover.wms.core.model.threepl;

import lombok.Data;

import java.util.Date;

@Data
public class StockMovement {

    private Long movementDocNo;
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
    private Date documentDate = new Date();
    private Double openingStock;
    private Double inboundQty;
    private Double outboundQty;
    private Double closingStock;
    private Double stockUom;
    private String partnerCode;
    private String description;
    private String companyDescription;
    private Long proformaInvoiceNo = 0L;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private Long statusId;
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
    private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
    private Date updatedOn;
    private Double inventoryQuantity;
    private String palletCode;
    private String caseCode;
    private String packBarcodes;
    private Long variantCode;
    private String variantSubCode;
    private String batchSerialNumber;
    private String storageBin;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String referenceOrderNo;
    private String storageMethod;
    private Long binClassId;
    private Double allocatedQuantity;
    private String inventoryUom;
    private Date manufacturerDate;
    private Date expiryDate;
    private String manufacturerCode;
    private String barcodeId;
    private String cbm;
    private String cbmUnit;
    private String cbmPerQuantity;
    private String manufacturerName;
    private String levelId;
    private String origin;
    private String brand;
    private String referenceDocumentNo;
    private String stockTypeDescription;

    //3PL
    private Long threePLStockIndicator;
    private String threePLPartnerId;
    private Date threePLGrDate = new Date();
    private Double threePLCbm = 0D;
    private String threePLUom;
    private Double threePLCbmPerQty = 0D;
    private Double threePLRatePerQty = 0D;
    private Double rate = 0D;
    private String currency;
    private Double totalThreePLCbm;
    private Double totalRate;
    private String threePLBillStatus;
    private Double threePLLength;
    private Double threePLHeight;
    private Double threePLWidth;
    private Long stockInvoice = 0L;

}
