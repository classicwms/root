package com.tekclover.wms.api.inbound.orders.model.impl;

import java.util.Date;

public interface PutAwayHeaderImpl {

     String getLanguageId();
     String getCompanyCodeId();
     String getPlantId();
     String getWarehouseId();
     String getPreInboundNo();
     String getRefDocNumber();
     String getGoodsReceiptNo();
     Long getInboundOrderTypeId();
     String getPalletCode();
     String getCaseCode();
     String getPackBarcodes();
     String getPutAwayNumber();
     String getProposedStorageBin();
     Double getPutAwayQuantity();
     String getPutAwayUom();
     Long getStrategyTypeId();
     String getStrategyNo();
     String getProposedHandlingEquipment();
     String getAssignedUserId();
     Long getStatusId();
     String getQuantityType();
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
     Long getDeletionIndicator();
     String getCreatedBy();
     Date getCreatedOn();
     String getUpdatedBy();
     Date getUpdatedOn();
     String getConfirmedBy();
     Date getConfirmedOn();
     Double getInventoryQuantity();
     String getBarcodeId();
     Date getManufacturerDate();
     Date getExpiryDate();
     String getManufacturerCode();
     String getManufacturerName();
     String getOrigin();
     String getBrand();
     Double getOrderQty();
     String getCbm();
     String getCbmUnit();
     Double getCbmQuantity();
     String getApprovalStatus();
     String getRemark();
     String getCompanyDescription();
     String getPlantDescription();
     String getWarehouseDescription();
     String getStatusDescription();
     String getActualPackBarcodes();
     String getMiddlewareId();
     String getMiddlewareTable();
     String getManufacturerFullName();
     String getReferenceDocumentType();
     Date getTransferOrderDate();
     String getIsCompleted();
     String getIsCancelled();
     Date getMUpdatedOn();
     String getSourceBranchCode();
     String getSourceCompanyCode();
     String getLevelId();
}