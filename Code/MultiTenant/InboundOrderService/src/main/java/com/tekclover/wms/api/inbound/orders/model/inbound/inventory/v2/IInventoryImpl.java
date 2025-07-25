package com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2;

import java.util.Date;

public interface IInventoryImpl {

	public String getLanguageId();
	public String getInventoryId();
	public String getCompanyCodeId();
	public String getPlantId();
	public String getWarehouseId();
	public String getPalletCode();
	public String getCaseCode();
	public String getPackBarcodes();
	public String getItemCode();
	public Long getVariantCode();
	public String getVariantSubCode();
	public String getBatchSerialNumber();
	public String getStorageBin();
	public Long getStockTypeId();
	public Long getSpecialStockIndicatorId();
	public String getReferenceOrderNo();
	public String getStorageMethod();
	public Long getBinClassId();
	public String getDescription();
	public Double getInventoryQuantity();
	public Double getAllocatedQuantity();
	public String getInventoryUom();
	public Date getManufacturerDate();
	public Date getExpiryDate();
	public Long getDeletionIndicator();
	public String getReferenceField1();
	public String getReferenceField2();
	public String getReferenceField3();
	public Double getReferenceField4();
	public String getReferenceField5();
	public String getReferenceField6();
	public String getReferenceField7();
	public String getReferenceField8();
	public String getReferenceField9();
	public String getReferenceField10();
	public String getCreatedBy();
	public Date getCreatedOn();
	public String getSCreatedOn();
	public String getUpdatedBy();
	public Date getUpdatedOn();
	public Date getBatchDate();
	public String getManufacturerCode();
	public String getBarcodeId();
	public String getCbm();
	public String getLevelId();
	public String getCbmUnit();
	public String getCbmPerQuantity();
	public String getManufacturerName();
	public String getOrigin();
	public String getBrand();
	public String getReferenceDocumentNo();
	public String getCompanyDescription();
	public String getPlantDescription();
	public String getWarehouseDescription();
	public String getStatusDescription();
	public String getStockTypeDescription();
	public String getPartnerCode();
	public String getStorageSectionId();
	public Long getItemType();
	public String getItemTypeDescription();
	Double getTotalQuantity();

	String getMaterialNo();
	String getPriceSegment();
	String getArticleNo();
	String getGender();
	String getColor();
	String getSize();
	String getNoPairs();

	String getAlternateUom();
	Double getNoBags();
	Double getBagSize();
	Double getMrp();
	String getItemGroup();

	//----------------------Namratha------------------------//
	Double getCaseQty();
	Boolean getLoosePack();


}