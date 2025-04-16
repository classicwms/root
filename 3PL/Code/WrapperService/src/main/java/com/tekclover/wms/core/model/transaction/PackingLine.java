package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class PackingLine {

    private String languageId;
    private String companyCodeId; // Changed from Long to String
    private String plantId;
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String partnerCode;
    private Long lineNumber;
    private Long packingNo;
    private String itemCode;
    private Long variantCode;
    private String variantSubCode;
    private String packCode;
    private String batchSerialNumber;
    private Long outboundOrderTypeId;
    private Long statusId;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private Long proformaInvoiceNo = 0L;
    private String statusDescription;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String description;
    private String packingMaterialNo;
    private Double packQtyPerItem;
    private Double numberOfPacks;
    private Boolean shrinkWrapReqd;
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
    private String packConfirmedBy;
    private Date packConfirmedOn; // Removed initialization
    private String packUpdatedBy;
    private Date packUpdatedOn; // Removed initialization
    private String packingReversedBy;
    private Date packingReversedOn; // Removed initialization

    // 3PL
    private Double threePLCbm;
    private String threePLUom;
    private String threePLBillStatus;
    private Double threePLLength;
    private Double threePLHeight;
    private Double threePLWidth;
    private Double rate;
    private String currency;
    private Double pickConfirmQty;
    private Double allocatedQty;
    private String pickUom;
    private Double totalThreePLCbm;
    private Double totalRate;
    private Long packingInvoice = 0L;
}
