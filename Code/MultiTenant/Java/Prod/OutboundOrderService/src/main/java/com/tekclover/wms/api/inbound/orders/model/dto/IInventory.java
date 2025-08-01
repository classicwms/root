package com.tekclover.wms.api.inbound.orders.model.dto;

import java.util.Date;

public interface IInventory {

	public String getStorageBin();
	public Double getInventoryQty();
	public String getInventoryUom();
	public Date getCreatedOn();
	public String getItemCode();
	public String getManufacturerName();
	public Long getLevelId();
	public String getPartnerCode();
	public String getBarcodeId();
	public String getDescription();
	public Long getInventoryId();
	public Double getAllocatedQty();
	public Date getExpiryDate();
	public Long getRemainingSelfLifePercentage();
}
