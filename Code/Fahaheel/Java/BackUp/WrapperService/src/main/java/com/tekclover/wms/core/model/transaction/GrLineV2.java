package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GrLineV2 extends GrLine {

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
    private Double cbmQuantity;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String interimStorageBin;
    private String middlewareId;
    private String middlewareTable;
    private String manufactureFullName;
    private String middlewareHeaderId;
    private String purchaseOrderNumber;
    private String referenceDocumentType;

    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private Long lineNumber;

    //threePl
    private Double threePLCbm;
    private String threePLUom;
    private String threePLBillStatus;
    private Double threePLLength;
    private Double threePLHeight;
    private Double threePLWidth;

    private String receiverName;
    private String unLoaderName;
    private String parentProductionOrderNo;
    
    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
    private String stagingNo;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;

    /*--------------------------Namratha--------------------------*/
    private String putAwayNumber;
    private Boolean crossDock;

    /*--------------------------REEFERON--------------------------*/
    private Double pieceQty;
    private Double caseQty;

    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCreate;
    private String vehicleNo;
    private Date vehicleReportingDate;
    private Date vehicleUnloadingDate;
    private String receivingVariance;

}