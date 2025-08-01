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
}
