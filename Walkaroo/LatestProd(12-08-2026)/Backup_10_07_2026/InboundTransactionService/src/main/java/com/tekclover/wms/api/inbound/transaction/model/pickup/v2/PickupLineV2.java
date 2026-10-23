package com.tekclover.wms.api.inbound.transaction.model.pickup.v2;

import com.tekclover.wms.api.inbound.transaction.model.pickup.PickupLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class PickupLineV2 extends PickupLine {

	@Column(name = "INV_QTY")
	private Double inventoryQuantity;

	@Column(name = "PICK_CBM")
	private Double pickedCbm;

	@Column(name = "CBM_UNIT", columnDefinition = "nvarchar(255)")
	private String cbmUnit;

    @Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
    private String manufacturerCode;

	@Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
	private String manufacturerName;

	@Column(name = "ST_SEC_ID")
	private String storageSectionId;

	@Column(name = "ORIGIN", columnDefinition = "nvarchar(255)")
	private String origin;

	@Column(name = "BRAND", columnDefinition = "nvarchar(255)")
	private String brand;

	@Column(name = "LEVEL_ID", columnDefinition = "nvarchar(255)")
	private String levelId;

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;

	@Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
	private String statusDescription;

	@Column(name = "MIDDLEWARE_ID")
	private Long middlewareId;

	@Column(name = "MIDDLEWARE_HEADER_ID")
	private Long middlewareHeaderId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
	private String referenceDocumentType;

	@Column(name = "SALES_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
	private String salesOrderNumber;

	@Column(name = "PICK_LIST_NUMBER", columnDefinition = "nvarchar(150)")
	private String pickListNumber;

	@Column(name = "TOKEN_NUMBER", columnDefinition = "nvarchar(150)")
	private String tokenNumber;

	@Column(name = "SALES_INVOICE_NUMBER", columnDefinition = "nvarchar(150)")
	private String salesInvoiceNumber;

	@Column(name = "SUPPLIER_INVOICE_NO", columnDefinition = "nvarchar(150)")
	private String supplierInvoiceNo;

	@Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
	private String manufacturerFullName;

	@Column(name = "TARGET_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String targetBranchCode;

	@Column(name = "VAR_QTY")
	private Double varianceQuantity;
	
	@Column(name = "CUSTOMER_ID", columnDefinition = "nvarchar(150)")
	private String customerId;

	@Column(name = "CUSTOMER_NAME", columnDefinition = "nvarchar(150)")
	private String customerName;
	
    /*----------------Walkaroo changes------------------------------------------------------*/
    @Column(name = "MATERIAL_NO", columnDefinition = "nvarchar(50)")
    private String materialNo;
    
    @Column(name = "PRICE_SEGMENT", columnDefinition = "nvarchar(50)")
    private String priceSegment;
    
    @Column(name = "ARTICLE_NO", columnDefinition = "nvarchar(50)")
    private String articleNo;
    
    @Column(name = "GENDER", columnDefinition = "nvarchar(50)")
    private String gender;
    
    @Column(name = "COLOR", columnDefinition = "nvarchar(50)")
    private String color;
    
    @Column(name = "SIZE", columnDefinition = "nvarchar(50)")
    private String size;
    
    @Column(name = "NO_PAIRS", columnDefinition = "nvarchar(50)")
    private String noPairs;

	@Column(name = "STG_AREA", columnDefinition = "nvarchar(100)")
	private String stagingArea;

	@Column(name = "PALLET_ID", columnDefinition = "nvarchar(100)")
	private String palletId;

	@Column(name = "SHIP_TO_CODE", columnDefinition = "nvarchar(50)")
	private String shipToCode;

	@Column(name = "SHIP_TO_PARTY", columnDefinition = "nvarchar(50)")
	private String shipToParty;

	@Column(name = "SPECIAL_STOCK", columnDefinition = "nvarchar(50)")
	private String specialStock;

	@Column(name = "MTO_NUMBER", columnDefinition = "nvarchar(50)")
	private String mtoNumber;
}