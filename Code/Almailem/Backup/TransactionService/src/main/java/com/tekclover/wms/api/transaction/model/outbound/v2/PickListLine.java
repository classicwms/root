package com.tekclover.wms.api.transaction.model.outbound.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblpicklistline")
public class PickListLine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PL_CANCEL_LINE_ID")
	private Long pickListCancelLineId;

	@Column(name = "PL_CANCEL_HEADER_ID")
	private Long pickListCancelHeaderId;
	
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Column(name = "C_ID")
	private String companyCodeId;
	
	@Column(name = "PLANT_ID")
	private String plantId;
	
	@Column(name = "WH_ID")
	private String warehouseId;
	
	@Column(name = "OLD_PRE_OB_NO")
	private String oldPreOutboundNo;

	@Column(name = "NEW_PRE_OB_NO")
	private String newPreOutboundNo;

	@Column(name = "OLD_REF_DOC_NO")
	private String oldRefDocNumber;

	@Column(name = "NEW_REF_DOC_NO")
	private String newRefDocNumber;
	
	@Column(name = "PARTNER_CODE")
	private String partnerCode;
	
	@Column(name = "OLD_OB_LINE_NO")
	private Long oldLineNo;

	@Column(name = "NEW_OB_LINE_NO")
	private Long newLineNo;
	
	@Column(name = "ITM_CODE")
	private String itemCode;
	
	@Column(name = "DLV_ORD_NO")
	private String deliveryOrderNo;
	
	@Column(name = "STR_NO") 
	private String batchSerialNumber;
	
	@Column(name = "OB_ORD_TYP_ID")
	private Long outboundOrderTypeId;
	
	@Column(name = "STATUS_ID") 
	private Long oldStatusId;

	@Column(name = "NEW_STATUS_ID")
	private Long newStatusId;
	
	@Column(name = "STCK_TYP_ID")
	private Long stockTypeId;
	
	@Column(name = "SP_ST_IND_ID") 
	private Long specialStockIndicatorId;
	
	@Column(name = "ITEM_TEXT") 
	private String description;

	@Column(name = "OLD_ORD_QTY")
	private Double oldOrderQty;

	@Column(name = "NEW_ORD_QTY")
	private Double newOrderQty;
	
	@Column(name = "ORD_UOM") 
	private String orderUom;
	
	@Column(name = "DLV_QTY") 
	private Double deliveryQty;

	@Column(name = "OLD_PICK_CNF_QTY")
	private Double oldPickConfirmQty;

	@Column(name = "OLD_PICK_ST_BIN")
	private String oldPickedStorageBin;

	@Column(name = "NEW_PICK_CNF_QTY")
	private Double newPickConfirmQty;

	@Column(name = "NEW_PICK_ST_BIN")
	private String newPickedStorageBin;
	
	@Column(name = "DLV_UOM")
	private String deliveryUom;
	
	@Column(name = "REF_FIELD_1") 
	private String referenceField1;
	
	@Column(name = "REF_FIELD_2")
	private String referenceField2;
	
	@Column(name = "REF_FIELD_3") 
	private String referenceField3;
	
	@Column(name = "REF_FIELD_4")
	private String referenceField4;
	
	@Column(name = "REF_FIELD_5")	
	private String referenceField5;
	
	@Column(name = "REF_FIELD_6")
	private String referenceField6;
	
	@Column(name = "REF_FIELD_7")
	private String referenceField7;
	
	@Column(name = "REF_FIELD_8")
	private String referenceField8;
	
	@Column(name = "REF_FIELD_9") 
	private String referenceField9;
	
	@Column(name = "REF_FIELD_10")
	private String referenceField10;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator;
	
	@Column(name = "DLV_CTD_BY")
	private String createdBy;
	
	@Column(name = "DLV_CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "DLV_CNF_BY")
	private String deliveryConfirmedBy;
	
	@Column(name = "DLV_CNF_ON")
	private Date deliveryConfirmedOn;
	
	@Column(name = "DLV_UTD_BY")
	private String updatedBy;
	
	@Column(name = "DLV_UTD_ON")
	private Date updatedOn;
	
	@Column(name = "DLV_REV_BY")
	private String reversedBy;
	
	@Column(name = "DLV_REV_ON") 
	private Date reversedOn;

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;

	@Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
	private String oldStatusDescription;
	@Column(name = "NEW_STATUS_TEXT", columnDefinition = "nvarchar(150)")
	private String newStatusDescription;

	@Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
	private String manufacturerName;

	@Column(name = "MIDDLEWARE_ID")
	private Long middlewareId;

	@Column(name = "MIDDLEWARE_HEADER_ID")
	private Long middlewareHeaderId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
	private String referenceDocumentType;

	@Column(name = "SUPPLIER_INVOICE_NO", columnDefinition = "nvarchar(150)")
	private String supplierInvoiceNo;

	@Column(name = "SALES_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
	private String salesOrderNumber;

	@Column(name = "MFR_FULL_NAME", columnDefinition = "nvarchar(150)")
	private String manufacturerFullName;

	@Column(name = "SALES_INVOICE_NUMBER", columnDefinition = "nvarchar(150)")
	private String salesInvoiceNumber;

	@Column(name = "PICK_LIST_NUMBER", columnDefinition = "nvarchar(150)")
	private String pickListNumber;

	@Column(name = "TOKEN_NUMBER", columnDefinition = "nvarchar(150)")
	private String tokenNumber;

	@Column(name = "INVOICE_DATE")
	private Date invoiceDate;

	@Column(name = "DELIVERY_TYPE", columnDefinition = "nvarchar(100)")
	private String deliveryType;

	@Column(name = "CUSTOMER_ID", columnDefinition = "nvarchar(150)")
	private String customerId;

	@Column(name = "CUSTOMER_NAME", columnDefinition = "nvarchar(150)")
	private String customerName;

	@Column(name = "ADDRESS", columnDefinition = "nvarchar(500)")
	private String address;

	@Column(name = "PHONE_NUMBER", columnDefinition = "nvarchar(100)")
	private String phoneNumber;

	@Column(name = "ALTERNATE_NO", columnDefinition = "nvarchar(100)")
	private String alternateNo;

	@Column(name = "STATUS", columnDefinition = "nvarchar(100)")
	private String status;

	/*---------------------------------------------------------------------------------------------------------*/

	@Column(name = "TRANSFER_ORDER_NO", columnDefinition = "nvarchar(50)")
	private String transferOrderNo;

	@Column(name = "RET_ORDER_NO", columnDefinition = "nvarchar(50)")
	private String returnOrderNo;

	@Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
	private String isCompleted;

	@Column(name = "IS_CANCELLED", columnDefinition = "nvarchar(20)")
	private String isCancelled;

	@Column(name = "TARGET_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String targetBranchCode;

	@Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(255)")
	private String barcodeId;

	@Column(name = "CUSTOMER_TYPE", columnDefinition = "nvarchar(255)")
	private String customerType;

	@Column(name = "HE_NO", columnDefinition = "nvarchar(255)")
	private String handlingEquipment;
}