package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class GrLineV2 {
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
    private Long lineNumber;
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
    private String itemDescription;
    private String manufacturerPartNo;
    private String hsnCode;
    private String variantType;
    private String specificationActual;
    private String itemBarcode;
    private Double orderQty;
    private String orderUom;
    private Double goodReceiptQty;
    private String grUom;
    private Double acceptedQty;
    private Double damageQty;
    private String quantityType;
    private String assignedUserId;
    private String putAwayHandlingEquipment;
    private Double confirmedQty;
    private Double remainingQty;
    private String referenceOrderNo;
    private Double referenceOrderQty;
    private Double crossDockAllocationQty;
    private Timestamp manufacturerDate;
    private Timestamp expiryDate;
    private Double storageQty;
    private String remark;
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
    private Timestamp createdOn ;
    private String updatedBy;
    private Timestamp updatedOn ;
    private String confirmedBy;
    private Timestamp confirmedOn ;
    private Double inventoryQuantity;
    private String barcodeId;
    private Double cbm;
    private String cbmUnit;
    private String manufacturerCode;
    private String manufacturerName;
    private String origin;
    private String brand;
    private String rejectType;
    private String rejectReason;
    private Double cbmQuantity;                                        //CBM per Quantity
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String interimStorageBin;
    private String statusDescription;
    private String purchaseOrderNumber;
    private String middlewareId;
    private String middlewareHeaderId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private Double threePLCbm;
    private String threePLUom;
    private String threePLBillStatus;
    private Double threePLLength;
    private Double threePLHeight;
    private Double threePLWidth;
    private Double acceptTotalCbm;
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;

    /*--------------------------Namratha--------------------------*/
    private String putAwayNumber;

    /*--------------------------REEFERON--------------------------*/
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
}