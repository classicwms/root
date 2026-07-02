package com.tekclover.wms.api.transaction.model.inbound.v2;

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
@Table(name = "tblsupplierinvoicecancelheader")
public class SupplierInvoiceHeader {

	@Id
	@Column(name = "SI_CANCEL_HEADER_ID")
	private Long supplierInvoiceCancelHeaderId;

	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
	private String languageId;
	
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
	private String companyCode;
	
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
	private String plantId;
	
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
	private String warehouseId;

	@Column(name = "OLD_REF_DOC_NO", columnDefinition = "nvarchar(100)")
	private String oldRefDocNumber;

	@Column(name = "NEW_REF_DOC_NO", columnDefinition = "nvarchar(100)")
	private String newRefDocNumber;

	@Column(name = "OLD_PRE_IB_NO", columnDefinition = "nvarchar(100)")
	private String oldPreInboundNo;

	@Column(name = "NEW_PRE_IB_NO", columnDefinition = "nvarchar(100)")
	private String newPreInboundNo;
	
	@Column(name = "STATUS_ID") 
	private Long oldStatusId;
	@Column(name = "NEW_STATUS_ID")
	private Long newStatusId;

	@Column(name = "IB_ORD_TYP_ID") 
	private Long inboundOrderTypeId;

	@Column(name = "OLD_CONT_NO", columnDefinition = "nvarchar(100)")
	private String oldContainerNo;

	@Column(name = "NEW_CONT_NO", columnDefinition = "nvarchar(100)")
	private String newContainerNo;
	
	@Column(name = "OLD_VEH_NO", columnDefinition = "nvarchar(100)")
	private String oldVechicleNo;

	@Column(name = "NEW_VEH_NO", columnDefinition = "nvarchar(100)")
	private String newVechicleNo;

	@Column(name = "IB_TEXT")
	private String headerText;
	
	@Column(name = "IS_DELETED") 
	private Long deletionIndicator;
	
	@Column(name = "REF_FIELD_1", columnDefinition = "nvarchar(255)")
	private String referenceField1;
	
	@Column(name = "REF_FIELD_2", columnDefinition = "nvarchar(255)")
	private String referenceField2;
	
	@Column(name = "REF_FIELD_3", columnDefinition = "nvarchar(255)")
	private String referenceField3;
	
	@Column(name = "REF_FIELD_4", columnDefinition = "nvarchar(255)")
	private String referenceField4;
	
	@Column(name = "REF_FIELD_5", columnDefinition = "nvarchar(255)")
	private String referenceField5;
	
	@Column(name = "REF_FIELD_6", columnDefinition = "nvarchar(255)")
	private String referenceField6;
	
	@Column(name = "REF_FIELD_7", columnDefinition = "nvarchar(255)")
	private String referenceField7;
	
	@Column(name = "REF_FIELD_8", columnDefinition = "nvarchar(255)")
	private String referenceField8;
	
	@Column(name = "REF_FIELD_9", columnDefinition = "nvarchar(255)")
	private String referenceField9;
	
	@Column(name = "REF_FIELD_10", columnDefinition = "nvarchar(255)")
	private String referenceField10;
	
	@Column(name = "CTD_BY", columnDefinition = "nvarchar(255)")
	private String createdBy;
	
	@Column(name = "CTD_ON")
	private Date createdOn;
	
	@Column(name = "UTD_BY", columnDefinition = "nvarchar(255)")
	private String updatedBy;
	
	@Column(name = "UTD_ON")
	private Date updatedOn;
	
	@Column(name = "IB_CNF_BY", columnDefinition = "nvarchar(255)")
	private String confirmedBy;
	
	@Column(name = "IB_CNF_ON")
	private Date confirmedOn;

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

	@Column(name = "PURCHASE_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
	private String purchaseOrderNumber;

	@Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
	private String middlewareId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "MFR_FULL_NAME", columnDefinition = "nvarchar(150)")
	private String manufacturerFullName;

	@Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
	private String referenceDocumentType;

	@Column(name = "OLD_COUNT_OF_ORD_LINES")
	private Long oldCountOfOrderLines;

	@Column(name = "OLD_RECEIVED_LINES")
	private Long oldReceivedLines;

	@Column(name = "NEW_COUNT_OF_ORD_LINES")
	private Long newCountOfOrderLines;

	@Column(name = "NEW_RECEIVED_LINES")
	private Long newReceivedLines;

	/*-------------------------------------------------------------------------------------------------------*/
	@Column(name = "TRANSFER_ORDER_DATE")
	private Date transferOrderDate;

	@Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
	private String isCompleted;

	@Column(name = "SOURCE_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String sourceBranchCode;

	@Column(name = "SOURCE_COMPANY_CODE", columnDefinition = "nvarchar(50)")
	private String sourceCompanyCode;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "supplierInvoiceCancelHeaderId", fetch = FetchType.EAGER)
	private List<SupplierInvoiceLine> line;
}