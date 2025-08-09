package com.tekclover.wms.core.model.transaction;


import lombok.Data;

@Data
public class CrossDockUnallocated {

    private String crossDockOrderManagementNo;

    private String languageId;

    private String companyCodeId;

    private String plantId;

    private String warehouseId;

    private String preOutboundNo;

    private String refDocNumber;

    private String partnerCode;

    private Long lineNumber;

    private String itemCode;

    private String proposedStorageBin;

    private String proposedPackBarCode; //proposedPackCode

    private String barcodeId;

    private Double orderQty;

    private String orderUom;

    private Double inventoryQty;

    private Double allocatedQty;

    private Double reAllocatedQty;

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

    private String pickupNumber;

    private Double noBags;

}
