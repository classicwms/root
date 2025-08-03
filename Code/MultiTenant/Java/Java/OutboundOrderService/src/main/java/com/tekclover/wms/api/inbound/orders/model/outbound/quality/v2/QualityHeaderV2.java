package com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2;

import com.tekclover.wms.api.inbound.orders.model.outbound.quality.QualityHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class QualityHeaderV2 extends QualityHeader {

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

	@Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
	private String manufacturerName;

	@Column(name = "ST_SEC_ID")
	private String storageSectionId;

	@Column(name = "TARGET_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String targetBranchCode;

	@Column(name = "NOTI_STATUS")
	private Long notificationStatus = 0L;

	@Column(name = "STR_NO")
	private String batchSerialNumber;

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
	/*----------------------Impex--------------------------------------------------*/
	@Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
	private String alternateUom;

	@Column(name = "NO_BAGS")
	private Double noBags;

	@Column(name = "BAG_SIZE")
	private Double bagSize;

	@Column(name = "MRP")
	private Double mrp;

	@Column(name = "ITM_TYP", columnDefinition = "nvarchar(100)")
	private String itemType;

	@Column(name = "ITM_GRP", columnDefinition = "nvarchar(100)")
	private String itemGroup;

	@Column(name = "BRAND", columnDefinition = "nvarchar(100)")
	private String brand;


	@Column(name = "IMS_SALE_TYP_CODE")
	private Integer imsSaleTypeCode;

	@Column(name = "CSTR_COD", columnDefinition = "nvarchar(50)")
	private String customerCode;

	@Column(name = "TFR_REQ_TYP", columnDefinition = "nvarchar(50)")
	private String TransferRequestType;

}