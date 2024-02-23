package com.tekclover.wms.api.transaction.model.outbound.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblpicklistheader")
public class PickListHeader {

	@Id
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
	
	@Column(name = "OLD_PRE_OB_NO", columnDefinition = "nvarchar(100)")
	private String oldPreOutboundNo;

	@Column(name = "NEW_PRE_OB_NO", columnDefinition = "nvarchar(100)")
	private String newPreOutboundNo;

	@Column(name = "OLD_REF_DOC_NO", columnDefinition = "nvarchar(100)")
	private String oldRefDocNumber;

	@Column(name = "NEW_REF_DOC_NO", columnDefinition = "nvarchar(100)")
	private String newRefDocNumber;
	
	@Column(name = "PARTNER_CODE")
	private String partnerCode;
	
	@Column(name = "DLV_ORD_NO")
	private String deliveryOrderNo;
	
	@Column(name = "REF_DOC_TYP")
	private String referenceDocumentType;
	
	@Column(name = "OB_ORD_TYP_ID") 
	private Long outboundOrderTypeId;
	
	@Column(name = "STATUS_ID")
	private Long oldStatusId;

	@Column(name = "NEW_STATUS_ID")
	private Long newStatusId;
	
	@Column(name = "REF_DOC_DATE")
	private Date refDocDate;
	
	@Column(name = "REQ_DEL_DATE")
	private Date requiredDeliveryDate;
	
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
	
	@Column(name = "REMARK")
	private String remarks;
	
	@Column(name = "DLV_CTD_BY") 
	private String createdBy;
	
	@Column(name = "DLV_CTD_ON") 
	private Date createdOn;
	
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

	@Column(name = "OLD_INVOICE_NO", columnDefinition = "nvarchar(100)")
	private String oldInvoiceNumber;

	@Column(name = "NEW_INVOICE_NO", columnDefinition = "nvarchar(100)")
	private String newInvoiceNumber;

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

	@Column(name = "MIDDLEWARE_ID")
	private Long middlewareId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "OLD_SALES_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
	private String oldSalesOrderNumber;

	@Column(name = "NEW_SALES_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
	private String newSalesOrderNumber;

	@Column(name = "OLD_SALES_INVOICE_NUMBER", columnDefinition = "nvarchar(150)")
	private String oldSalesInvoiceNumber;

	@Column(name = "NEW_SALES_INVOICE_NUMBER", columnDefinition = "nvarchar(150)")
	private String newSalesInvoiceNumber;

	@Column(name = "OLD_SUPPLIER_INVOICE_NO", columnDefinition = "nvarchar(150)")
	private String oldSupplierInvoiceNo;

	@Column(name = "NEW_SUPPLIER_INVOICE_NO", columnDefinition = "nvarchar(150)")
	private String newSupplierInvoiceNo;

	@Column(name = "OLD_PICK_LIST_NUMBER", columnDefinition = "nvarchar(150)")
	private String oldPickListNumber;

	@Column(name = "NEW_PICK_LIST_NUMBER", columnDefinition = "nvarchar(150)")
	private String newPickListNumber;

	@Column(name = "OLD_TOKEN_NUMBER", columnDefinition = "nvarchar(150)")
	private String oldTokenNumber;

	@Column(name = "NEW_TOKEN_NUMBER", columnDefinition = "nvarchar(150)")
	private String newTokenNumber;

	@Column(name = "OLD_INVOICE_DATE")
	private Date oldInvoiceDate;

	@Column(name = "NEW_INVOICE_DATE")
	private Date newInvoiceDate;

	@Column(name = "DELIVERY_TYPE", columnDefinition = "nvarchar(100)")
	private String deliveryType;

	@Column(name = "OLD_CUSTOMER_ID", columnDefinition = "nvarchar(150)")
	private String oldCustomerId;

	@Column(name = "NEW_CUSTOMER_ID", columnDefinition = "nvarchar(150)")
	private String newCustomerId;

	@Column(name = "OLD_CUSTOMER_NAME", columnDefinition = "nvarchar(150)")
	private String oldCustomerName;

	@Column(name = "NEW_CUSTOMER_NAME", columnDefinition = "nvarchar(150)")
	private String newCustomerName;

	@Column(name = "ADDRESS", columnDefinition = "nvarchar(500)")
	private String address;

	@Column(name = "PHONE_NUMBER", columnDefinition = "nvarchar(100)")
	private String phoneNumber;

	@Column(name = "ALTERNATE_NO", columnDefinition = "nvarchar(100)")
	private String alternateNo;

	@Column(name = "STATUS", columnDefinition = "nvarchar(100)")
	private String status;

	/*-------------------------------------------------------------------------------------------------*/

	@Column(name = "FROM_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String fromBranchCode;

	@Column(name = "TARGET_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String targetBranchCode;

	@Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
	private String isCompleted;

	@Column(name = "IS_CANCELLED", columnDefinition = "nvarchar(20)")
	private String isCancelled;

	@Column(name = "OLD_ORD_LINE_COUNT")
	private Long oldCountOfOrderedLine;

	@Column(name = "OLD_PICK_LINE_COUNT")
	private Long oldCountOfPickedLine;

	@Column(name = "NEW_ORD_LINE_COUNT")
	private Long newCountOfOrderedLine;

	@Column(name = "NEW_PICK_LINE_COUNT")
	private Long newCountOfPickedLine;

	@Column(name = "SUM_PICK_QTY")
	private Long sumOfPickedQty;

	@Column(name = "CUSTOMER_TYPE", columnDefinition = "nvarchar(255)")
	private String customerType;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pickListCancelHeaderId", fetch = FetchType.EAGER)
	private List<PickListLine> line;
}