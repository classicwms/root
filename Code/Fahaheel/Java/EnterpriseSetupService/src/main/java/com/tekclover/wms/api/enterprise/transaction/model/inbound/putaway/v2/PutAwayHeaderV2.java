package com.tekclover.wms.api.enterprise.transaction.model.inbound.putaway.v2;

import com.tekclover.wms.api.enterprise.transaction.model.inbound.putaway.PutAwayHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
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

	@Column(name = "LEVEL_ID", columnDefinition = "nvarchar(255)")
	private String levelId;
}