package com.tekclover.wms.api.transaction.model.inbound.putaway.v2;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
//@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@ToString(callSuper = true)
public class PutAwayHeaderV2 extends PutAwayHeader {

	@Column(name = "INV_QTY")
	private Double inventoryQuantity;

	@Column(name = "BARCODE_ID", columnDefinition = "nvarchar(255)")
	private String barcodeId;

	@Column(name = "MFR_DATE")
	private Date manufacturerDate;

	@Column(name = "EXP_DATE")
	private Date expiryDate;

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

	@Column(name = "ORD_QTY")
	private Double orderQty;

	@Column(name = "CBM", columnDefinition = "nvarchar(255)")
	private String cbm;

	@Column(name = "CBM_UNIT", columnDefinition = "nvarchar(255)")
	private String cbmUnit;

	@Column(name = "CBM_QTY")
	private Double cbmQuantity;

	@Column(name = "APP_STATUS", columnDefinition = "nvarchar(255)")
	private String approvalStatus;

	@Column(name = "REMARK", columnDefinition = "nvarchar(255)")
	private String remark;

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;

	@Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
	private String statusDescription;

	@Column(name = "ACTUAL_PACK_BARCODE")
	private String actualPackBarcodes;

	@Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
	private String middlewareId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
	private String manufacturerFullName;

	@Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
	private String referenceDocumentType;

	/*-------------------------------------------------------------------------------------------------------*/
	@Column(name = "TRANSFER_ORDER_DATE")
	private Date transferOrderDate;

	@Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
	private String isCompleted;

	@Column(name = "IS_CANCELLED", columnDefinition = "nvarchar(20)")
	private String isCancelled;

	@Column(name = "M_UPDATED_ON")
	private Date mUpdatedOn;

	@Column(name = "SOURCE_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String sourceBranchCode;

	@Column(name = "SOURCE_COMPANY_CODE", columnDefinition = "nvarchar(50)")
	private String sourceCompanyCode;

	@Column(name = "PARENT_PRODUCTION_ORDER_NO", columnDefinition = "nvarchar(50)")
	private String parentProductionOrderNo;

	@Column(name = "LEVEL_ID", columnDefinition = "nvarchar(255)")
	private String levelId;
	@Column(name = "PARTNER_CODE")
	private String businessPartnerCode;

	@Column(name = "STR_NO")
	private String batchSerialNumber;
	
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

	@Column(name = "PAL_ID", columnDefinition = "nvarchar(100)")
	private String palletId;

	@Column(name = "GROUP_COUNT")
	private Long groupCount;

	@Column(name = "TEAM_MEM_1", columnDefinition = "nvarchar(200)")
	private String teamMember1;

	@Column(name = "TEAM_MEM_2", columnDefinition = "nvarchar(200)")
	private String teamMember2;

	@Column(name = "TEAM_MEM_3", columnDefinition = "nvarchar(200)")
	private String teamMember3;

	@Column(name = "TEAM_MEM_4", columnDefinition = "nvarchar(200)")
	private String teamMember4;

	@Column(name = "TEAM_MEM_5", columnDefinition = "nvarchar(200)")
	private String teamMember5;
}