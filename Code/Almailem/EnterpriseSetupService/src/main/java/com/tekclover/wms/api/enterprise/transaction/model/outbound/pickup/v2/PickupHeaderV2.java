package com.tekclover.wms.api.enterprise.transaction.model.outbound.pickup.v2;

import com.tekclover.wms.api.enterprise.transaction.model.outbound.pickup.PickupHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class PickupHeaderV2 extends PickupHeader {

	@Column(name = "INV_QTY")
	private Double inventoryQuantity;

	@Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
	private String manufacturerCode;

	@Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
	private String manufacturerName;

	@Column(name = "ORIGIN", columnDefinition = "nvarchar(255)")
	private String origin;

	@Column(name = "BRAND", columnDefinition = "nvarchar(255)")
	private String brand;

	@Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(255)")
	private String barcodeId;

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

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
	private String referenceDocumentType;

	@Column(name = "SALES_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
	private String salesOrderNumber;

	@Column(name = "SALES_INVOICE_NUMBER", columnDefinition = "nvarchar(150)")
	private String salesInvoiceNumber;

	@Column(name = "SUPPLIER_INVOICE_NO", columnDefinition = "nvarchar(150)")
	private String supplierInvoiceNo;

	@Column(name = "PICK_LIST_NUMBER", columnDefinition = "nvarchar(150)")
	private String pickListNumber;

	@Column(name = "TOKEN_NUMBER", columnDefinition = "nvarchar(150)")
	private String tokenNumber;

	@Column(name = "FROM_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String fromBranchCode;

	@Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
	private String isCompleted;

	@Column(name = "IS_CANCELLED", columnDefinition = "nvarchar(20)")
	private String isCancelled;

	@Column(name = "M_UPDATED_ON")
	private Date mUpdatedOn;

	@Column(name = "TARGET_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String targetBranchCode;

	@Column(name = "NOTI_STATUS")
	private Long notificationStatus = 0L;

	@Column(name = "IMS_SALE_TYP_CODE")
	private Integer imsSaleTypeCode;

}