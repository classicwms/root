package com.tekclover.wms.api.inbound.transaction.model.outbound.v2;

import java.util.Date;

public interface OutboundLineOutput {

	public String getLanguageId();
	public String getCompanyCodeId();
	public String getPlantId();
	public String getWarehouseId();
	public String getPreOutboundNo();
	public String getRefDocNumber();
	public String getPartnerCode();
	public String getHandlingEquipment();
	public Long getLineNumber();
	public String getItemCode();
	public String getCustomerType();
	public String getDeliveryOrderNo();
	public String getBatchSerialNumber();
	public Long getVariantCode();
	public String getVariantSubCode();
	public Long getOutboundOrderTypeId();
	public Long getStatusId();
	public Long getStockTypeId();
	public Long getSpecialStockIndicatorId();
	public String getDescription();
	public Double getOrderQty();
	public String getOrderUom();
	public Double getDeliveryQty();
	public String getDeliveryUom();
	public String getReferenceField1();
	public String getReferenceField2();
	public String getReferenceField3();
	public String getReferenceField4();
	public String getReferenceField5();
	public String getReferenceField6();
	public String getReferenceField7();
	public String getReferenceField8();
	public String getReferenceField9();
	public String getReferenceField10();
	public Long getDeletionIndicator();
	public String getCreatedBy();
	public Date getCreatedOn();
	public String getDeliveryConfirmedBy();
	public Date getDeliveryConfirmedOn();
	public String getUpdatedBy();
	public Date getUpdatedOn();
	public String getReversedBy();
	public Date getReversedOn();
	public String getItemText();
	public String getManufacturerName();
	public String getCompanyDescription();
	public String getPlantDescription();
	public String getWarehouseDescription();
	public String getStatusDescription();
	public Long getMiddlewareId();
	public Long getMiddlewareHeaderId();
	public String getMiddlewareTable();
	public String getReferenceDocumentType();
	public String getSupplierInvoiceNo();
	public String getSalesOrderNumber();
	public String getManufacturerFullName();

	public String getPickListNumber();
	public Date getInvoiceDate();
	public String getDeliveryType();
	public String getCustomerId();
	public String getCustomerName();
	public String getAddress();
	public String getPhoneNumber();
	public String getAlternateNo();
	public String getStatus();
	public String getTargetBranchCode();
	public String getBarcodeId();
	public String getSalesInvoiceNumber();
	public String getAssignedPickerId();
	public String getTracking();

	String getMaterialNo();
	String getPriceSegment();
	String getArticleNo();
	String getGender();
	String getColor();
	String getSize();
	String getNoPairs();

	Double getNoBags();
	Double getBagSize();
}