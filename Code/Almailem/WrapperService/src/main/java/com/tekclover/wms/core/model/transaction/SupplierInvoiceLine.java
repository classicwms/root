package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import java.util.Date;

@Data
public class SupplierInvoiceLine {

	private Long supplierInvoiceCancelLineId;
	private Long supplierInvoiceCancelHeaderId;
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String oldRefDocNumber;
	private String newRefDocNumber;
	private String oldPreInboundNo;
	private String newPreInboundNo;
	private Long oldLineNo;
	private Long newLineNo;
	private String itemCode;
	private Double oldOrderQty;
	private Double newOrderQty;
	private String orderUom;
	private Double acceptedQty;
	private Double damageQty;
	private Double oldPutawayConfirmedQty;
	private Double newPutawayConfirmedQty;
	private Double varianceQty;
	private Long inboundOrderTypeId;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String oldReferenceOrderNo;
	private String newReferenceOrderNo;
//	private Long statusId;
	private String oldContainerNo;
	private String newContainerNo;
	private String oldInvoiceNo;
	private String newInvoiceNo;
	private String description;
	private String manufacturerPartNo;
	private String hsnCode;
	private String itemBarcode;
	private Double itemCaseQty; // PACK_QTY in AX_API
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
	private String confirmedBy;
	private Date confirmedOn;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private Long oldStatusId;
	private Long newStatusId;
	private String oldStatusDescription;
	private String newStatusDescription;
	private String manufacturerCode;
	private String manufacturerName;
	private String middlewareId;
	private String middlewareHeaderId;
	private String middlewareTable;
	private String manufacturerFullName;
	private String referenceDocumentType;
	private String purchaseOrderNumber;
	private String supplierName;
	/*--------------------------------------------------------------------------------------------------------*/
	private String branchCode;
	private String transferOrderNo;
	private String isCompleted;
	private String oldPutAwayHandlingEquipment;
	private Double oldPutAwayQuantity;
	private String oldProposedStorageBin;
	private String oldConfirmedStorageBin;
	private String newPutAwayHandlingEquipment;
	private Double newPutAwayQuantity;
	private String newProposedStorageBin;
	private String newConfirmedStorageBin;
}