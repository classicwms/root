package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PutAwayLineV2 extends PutAwayLine {

	private Double inventoryQuantity;
	private String barcodeId;
	private Date manufacturerDate;
	private Date expiryDate;
	private String manufacturerCode;
	private String manufacturerName;
	private String origin;
	private String brand;
	private Double orderQty;
	private String cbm;
	private String cbmUnit;
	private Double cbmQuantity;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;
	private String businessPartnerCode;

	private String middlewareId;
	private String middlewareHeaderId;
	private String middlewareTable;
	private String purchaseOrderNumber;
	private String manufactureFullName;
	private String parentProductionOrderNo;
    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
	private String palletId;
	private Date assignedOn;
	private Long groupCount;
	private String teamMember1;
	private String teamMember2;
	private String teamMember3;
	private String teamMember4;
	private String teamMember5;
}