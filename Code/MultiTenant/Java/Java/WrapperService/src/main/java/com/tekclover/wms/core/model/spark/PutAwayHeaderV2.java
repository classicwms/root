package com.tekclover.wms.core.model.spark;

import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;

@Data
public class PutAwayHeaderV2 {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preInboundNo;
    private String refDocNumber;
    private String goodsReceiptNo;
    private Long inboundOrderTypeId;
    private String palletCode;
    private String caseCode;
    private String packBarcodes;
    private String putAwayNumber;
    private String proposedStorageBin;
    private Double putAwayQuantity;
    private String putAwayUom;
    private Long strategyTypeId;
    private String strategyNo;
    private String proposedHandlingEquipment;
    private String assignedUserId;
    private Long statusId;
    private String quantityType;
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
    private Timestamp createdOn;
    private String updatedBy;
    private Timestamp updatedOn;
    private String confirmedBy;
    private Timestamp confirmedOn;

    private Double inventoryQuantity;
    private String barcodeId;
    private Timestamp manufacturerDate;
    private Timestamp expiryDate;
    private String manufacturerCode;
    private String manufacturerName;
    private String origin;
    private String brand;
    private Double orderQty;
    private String cbm;
    private String cbmUnit;
    private Double cbmQuantity;
    private String approvalStatus;
    private String remark;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String actualPackBarcodes;
    private String middlewareId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private Timestamp transferOrderDate;
    private String isCompleted;
    private String isCancelled;
    private Timestamp mUpdatedOn;
    private String sourceBranchCode;
    private String sourceCompanyCode;
    private String levelId;
    private String businessPartnerCode;
    private String batchSerialNumber;
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;
    private Double pieceQty;
    private Double caseQty;
    /*-------------------------------------------Reeferon-----------------------------------*/

    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCreate;
    private String vehicleNo;
    private Timestamp vehicleReportingDate;
    private Timestamp vehicleUnloadingDate;
    private String receivingVariance;
    private String customerId;
    private String customerName;
}