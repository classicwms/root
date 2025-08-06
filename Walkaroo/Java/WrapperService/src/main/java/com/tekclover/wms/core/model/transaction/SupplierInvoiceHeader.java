package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class SupplierInvoiceHeader {

	private Long supplierInvoiceCancelHeaderId;
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String oldRefDocNumber;
	private String newRefDocNumber;
	private String oldPreInboundNo;
	private String newPreInboundNo;
//	private Long statusId;
	private Long inboundOrderTypeId;
	private String oldContainerNo;
	private String newContainerNo;
	private String oldVechicleNo;
	private String newVechicleNo;
	private String headerText;
	private Long deletionIndicator;
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
	private String purchaseOrderNumber;
	private String middlewareId;
	private String middlewareTable;
	private String manufacturerFullName;
	private String referenceDocumentType;
	private Long oldCountOfOrderLines;
	private Long oldReceivedLines;
	private Long newCountOfOrderLines;
	private Long newReceivedLines;
	/*-------------------------------------------------------------------------------------------------------*/
	private Date transferOrderDate;
	private String isCompleted;
	private String sourceBranchCode;
	private String sourceCompanyCode;
	private List<SupplierInvoiceLine> line;
}