package com.tekclover.wms.api.transaction.model.dto;

import java.util.Date;

public interface HHTUser {

	public String getStorageBin();
	public Double getInventoryQty();
	public String getInventoryUom();

	public String getUserId();

	public String getLanguageId();

	public String getCompanyCodeId();

	public String getPlantId();

	public String getWarehouseId();

	public Long getLevelId();

	public String getPassword();

	public String getUserName();

	public Date getStartDate();

	public Date getEndDate();

	public String getUserPresent();

	public String getNoOfDaysLeave();

}