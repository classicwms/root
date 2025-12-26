package com.tekclover.wms.api.enterprise.transaction.model.inbound.staging;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddStagingLine {

	private String languageId;
	private String companyCode;
	private String plantId;
	
	@NotBlank(message = "WarehouseId is mandatory")
	private String warehouseId;
	
	@NotBlank(message = "PreInboundNo is mandatory")
	private String preInboundNo;
	
	@NotBlank(message = "RefDocNumber is mandatory")
	private String refDocNumber;
	
	@NotBlank(message = "StagingNo is mandatory")
	private String stagingNo;
	
	private String palletCode;
	
	private List<String> caseCode;
	
	@NotNull(message = "LineNo is mandatory")
	private Long lineNo;
	
	@NotBlank(message = "ItemCode is mandatory")
	private String itemCode;
	private Long inboundOrderTypeId;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String storageMethod;
	private Long statusId;
	private String businessPartnerCode;
	private String containerNo;
	private String invoiceNo;
	private Double orderQty;
	private String orderUom;
	private Double itemQtyPerPallet;
	private Double itemQtyPerCase;
	private String assignedUserId;
	private String itemDescription;
	private String manufacturerPartNo;
	private String hsnCode;
	private String variantType;
	private String specificationActual;
	private String itemBarcode;
	private String referenceOrderNo;
	private Double referenceOrderQty;
	private Double crossDockAllocationQty;
	private String remarks;
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
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
	private String confirmedBy;
	private Date confirmedOn = new Date();
}