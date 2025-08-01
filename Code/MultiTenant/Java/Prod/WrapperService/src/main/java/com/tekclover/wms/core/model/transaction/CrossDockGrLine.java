package com.tekclover.wms.core.model.transaction;


import lombok.Data;

@Data
public class CrossDockGrLine {

    private String crossDockGrNumber;

    private String languageId;

    private String companyCode;

    private String plantId;

    private String warehouseId;

    private String preInboundNo;

    private String refDocNumber;

    private String goodsReceiptNo;

    private String palletCode;

    private String caseCode;

    private String packBarcodes;

    private Long lineNo;

    private Double noBags;

    private String itemCode;

    private Double orderQty;

    private Double goodReceiptQty;

    private Double acceptedQty;

    private Double damageQty;

    private String quantityType;

    private Double remainingQty;

    private Double referenceOrderQty;

    private Double crossDockAllocationQty;

    private Double storageQty;

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

    private Double inventoryQuantity;

    private String putAwayNumber;
}

