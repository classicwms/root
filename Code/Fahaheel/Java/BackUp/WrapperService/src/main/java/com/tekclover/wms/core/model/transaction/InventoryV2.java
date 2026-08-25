package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@Data
@ToString(callSuper = true)
public class InventoryV2 extends Inventory {

	private Long inventoryId;
	private String barcodeId;
	private String cbm;
	private String cbmUnit;
	private String cbmPerQuantity;
	private String manufacturerCode;
	private String manufacturerName;
	private String origin;
	private String brand;
	private String referenceDocumentNo;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
//	private String statusDescription;
	private String levelId;
	private String partnerCode;
	private Long itemType;
	private String itemTypeDescription;

	private String sCreatedOn;

    private String stockTypeDescription;
	private String statusDescription;
	private String businessPartnerCode;
	//threePl
	private Double threePLCbm;
	private String threePLUom;
	private String threePLBillStatus;
	private Double threePLLength;
	private Double threePLHeight;
	private Double threePLWidth;
	private Date batchDate;

	/*----------------Walkaroo changes------------------------------------------------------*/
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
	private String itemGroup;
	private Double caseQty;
	private boolean loosePack;
	/*--------------------------REEFERON--------------------------*/
	private Double pieceQty;
	private Double crateQty;
	private Double qtyInCase;
	private Double qtyInPiece;
	private Double qtyInCreate;
	private String vehicleNo;
	private Date manufacturerDate;
	private Date vehicleReportingDate;
	private Date vehicleUnloadingDate;
	private String selfLife;
	private String remainingDays;
	private Long remainingSelfLifePercentage;
	private String receivingVariance;
	private String customerId;
	private String customerName;

}