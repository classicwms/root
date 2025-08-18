package com.tekclover.wms.api.outbound.transaction.model.inbound.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblsupplierinvoicecancelline")
public class SupplierInvoiceLine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SI_CANCEL_LINE_ID")
	private Long supplierInvoiceCancelLineId;

	@Column(name = "SI_CANCEL_HEADER_ID")
	private Long supplierInvoiceCancelHeaderId;

	@Column(name = "LANG_ID")
	private String languageId;
	
	@Column(name = "C_ID")
	private String companyCode;
	
	@Column(name = "PLANT_ID")
	private String plantId;
	
	@Column(name = "WH_ID")
	private String warehouseId;
	
	@Column(name = "OLD_REF_DOC_NO")
	private String oldRefDocNumber;

	@Column(name = "NEW_REF_DOC_NO")
	private String newRefDocNumber;
	
	@Column(name = "OLD_PRE_IB_NO")
	private String oldPreInboundNo;

	@Column(name = "NEW_PRE_IB_NO")
	private String newPreInboundNo;
	
	@Column(name = "OLD_IB_LINE_NO")
	private Long oldLineNo;

	@Column(name = "NEW_IB_LINE_NO")
	private Long newLineNo;
	
	@Column(name = "ITM_CODE")
	private String itemCode;
	
	@Column(name = "OLD_ORD_QTY")
	private Double oldOrderQty;

	@Column(name = "NEW_ORD_QTY")
	private Double newOrderQty;

	@Column(name = "ORD_UOM") 
	private String orderUom;
	
	@Column(name = "ACCEPT_QTY") 
	private Double acceptedQty;
	
	@Column(name = "DAMAGE_QTY") 
	private Double damageQty;
	
	@Column(name = "OLD_PA_CNF_QTY")
	private Double oldPutawayConfirmedQty;

	@Column(name = "NEW_PA_CNF_QTY")
	private Double newPutawayConfirmedQty;
	
	@Column(name = "VAR_QTY") 
	private Double varianceQty;
	
	@Column(name = "IB_ORD_TYP_ID")
	private Long inboundOrderTypeId;
	
	@Column(name = "STCK_TYP_ID")
	private Long stockTypeId;
	
	@Column(name = "SP_ST_IND_ID") 
	private Long specialStockIndicatorId;
	
	@Column(name = "OLD_REF_ORD_NO")
	private String oldReferenceOrderNo;

	@Column(name = "NEW_REF_ORD_NO")
	private String newReferenceOrderNo;

	@Column(name = "STATUS_ID")
	private Long oldStatusId;
	@Column(name = "NEW_STATUS_ID")
	private Long newStatusId;
	
	@Column(name = "OLD_CONT_NO")
	private String oldContainerNo;

	@Column(name = "NEW_CONT_NO")
	private String newContainerNo;

	@Column(name = "OLD_INV_NO")
	private String oldInvoiceNo;

	@Column(name = "NEW_INV_NO")
	private String newInvoiceNo;

	@Column(name = "TEXT") 
	private String description;
	
	@Column(name = "MFR_PART")
	private String manufacturerPartNo;
	
	@Column(name = "HSN_CODE")
	private String hsnCode;
	
	@Column(name = "ITM_BARCODE")
	private String itemBarcode;
	
	@Column(name = "ITM_CASE_QTY")
	private Double itemCaseQty; // PACK_QTY in AX_API
	
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
	
	@Column(name = "CTD_BY")
	private String createdBy;
	
	@Column(name = "CTD_ON")
	private Date createdOn;
	
	@Column(name = "UTD_BY")
	private String updatedBy;
	
	@Column(name = "UTD_ON")
	private Date updatedOn;
	
	@Column(name = "IB_CNF_BY")
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

	@Column(name = "MFR_CODE", columnDefinition = "nvarchar(255)")
	private String manufacturerCode;

	@Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
	private String manufacturerName;

	@Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
	private String middlewareId;

	@Column(name = "MIDDLEWARE_HEADER_ID", columnDefinition = "nvarchar(50)")
	private String middlewareHeaderId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "MFR_FULL_NAME", columnDefinition = "nvarchar(150)")
	private String manufacturerFullName;

	@Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
	private String referenceDocumentType;

	@Column(name = "PURCHASE_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
	private String purchaseOrderNumber;

	@Column(name = "SUPPLIER_NAME", columnDefinition = "nvarchar(255)")
	private String supplierName;

	/*--------------------------------------------------------------------------------------------------------*/
	@Column(name = "BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String branchCode;

	@Column(name = "TRANSFER_ORDER_NO", columnDefinition = "nvarchar(50)")
	private String transferOrderNo;

	@Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
	private String isCompleted;

	@Column(name = "OLD_PAWAY_HE_NO")
	private String oldPutAwayHandlingEquipment;

	@Column(name = "OLD_PA_QTY")
	private Double oldPutAwayQuantity;

	@Column(name = "OLD_PROP_ST_BIN")
	private String oldProposedStorageBin;

	@Column(name = "OLD_CNF_ST_BIN")
	private String oldConfirmedStorageBin;

	@Column(name = "NEW_PAWAY_HE_NO")
	private String newPutAwayHandlingEquipment;

	@Column(name = "NEW_PA_QTY")
	private Double newPutAwayQuantity;

	@Column(name = "NEW_PROP_ST_BIN")
	private String newProposedStorageBin;

	@Column(name = "NEW_CNF_ST_BIN")
	private String newConfirmedStorageBin;
}