package com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2;

import java.util.Date;

public interface OrderManagementLineImpl {

	String getCompanyCodeId();
	String getPlantId();
	String getLanguageId();
	String getWarehouseId();
	String getPreOutboundNo();
	String getRefDocNumber();
	String getPartnerCode();
	Long getLineNumber();
	String getItemCode();
	String getProposedStorageBin();
	String getProposedPackBarCode();
	String getPickupNumber();
	Long getVariantCode();
	String getVariantSubCode();
	Long getOutboundOrderTypeId();
	Long getStatusId();
	Long getStockTypeId();
	Long getSpecialStockIndicatorId();
	String getDescription();
	String getManufacturerPartNo();
	String getHsnCode();
	String getItemBarcode();
	Double getOrderQty();
	String getOrderUom();
	Double getInventoryQty();
	Double getAllocatedQty();
	Double getReAllocatedQty();
	Long getStrategyTypeId();
	String getStrategyNo();
	Date getRequiredDeliveryDate();
	String getProposedBatchSerialNumber();
	String getProposedPalletCode();
	String getProposedCaseCode();
	String getProposedHeNo();
	String getProposedPicker();
	String getAssignedPickerId();
	String getReassignedPickerId();
	Long getDeletionIndicator();
	String getReferenceField1();
	String getReferenceField2();
	String getReferenceField3();
	String getReferenceField4();
	String getReferenceField5();
	String getReferenceField6();
	String getReferenceField7();
	String getReferenceField8();
	String getReferenceField9();
	String getReferenceField10();
	String getReAllocatedBy();
	Date getReAllocatedOn();
	String getPickupCreatedBy();
	Date getPickupCreatedOn();
	String getPickupUpdatedBy();
	Date getPickupUpdatedOn();
	String getPickerAssignedBy();
	Date getPickerAssignedOn();
	String getPickerReassignedBy();
	Date getPickerReassignedOn();
	String getManufacturerCode();
	String getManufacturerName();
	String getOrigin();
	String getBrand();
	String getBarcodeId();
	String getLevelId();
	String getCompanyDescription();
	String getPlantDescription();
	String getWarehouseDescription();
	String getStatusDescription();
	Long getMiddlewareId();
	Long getMiddlewareHeaderId();
	String getMiddlewareTable();
	String getReferenceDocumentType();
	String getSalesInvoiceNumber();
	String getSupplierInvoiceNo();
	String getSalesOrderNumber();
	String getPickListNumber();
	String getTokenNumber();
	String getManufacturerFullName();
	String getTransferOrderNo();
	String getReturnOrderNo();
	String getIsCompleted();
	String getIsCancelled();
	String getTargetBranchCode();
	Integer getImsSaleTypeCode();
}