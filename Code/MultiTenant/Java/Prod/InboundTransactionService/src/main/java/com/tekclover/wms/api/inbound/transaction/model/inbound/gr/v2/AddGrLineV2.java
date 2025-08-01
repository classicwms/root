package com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2;

import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.AddGrLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class AddGrLineV2 extends AddGrLine {

    private Double inventoryQuantity;
    private String barcodeId;
    //    private Double cbm;
    private String cbmUnit;
    private String manufacturerCode;
    private String manufacturerName;
    private String storageSectionId;
    private String origin;
    private String brand;
    private String rejectType;
    private String rejectReason;
    //    private Double cbmQuantity;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String interimStorageBin;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private String middlewareId;
    private String middlewareHeaderId;
    private String middlewareTable;
    private String purchaseOrderNumber;
    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private Double threePLCbm;
    private String threePLUom;
    private String threePLBillStatus;
    private Double threePLLength;
    private Double threePLHeight;
    private Double threePLWidth;
    
    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
	private String statusDescription;
	private String putAwayNumber;
    private String parentProductionOrderNo;

    private String stagingNo;
    /*----------------------Impex--------------------------------------------------*/
    private String alternateUom;
    private Double noBags;
    private Double bagSize;
    private Double mrp;
    private String itemType;
    private String itemGroup;
    private String partner_item_barcode;
    /*----------------------Reeferon--------------------------------------------------*/
    private Double pieceQty;
    private Double caseQty;
    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCreate;
    private String vehicleNo;
    private Date vehicleReportingDate;
    private Date vehicleUnloadingDate;
    private String receivingVariance;
    private String newCreated;
    private String customerId;
    private  String customerName;
}