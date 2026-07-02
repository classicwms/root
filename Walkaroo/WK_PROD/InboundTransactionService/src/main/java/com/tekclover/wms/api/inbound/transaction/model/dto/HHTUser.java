package com.tekclover.wms.api.inbound.transaction.model.dto;

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

	public String getTeamMember1();
	public String getTeamMember2();
	public String getTeamMember3();
	public String getTeamMember4();
	public String getTeamMember5();
}