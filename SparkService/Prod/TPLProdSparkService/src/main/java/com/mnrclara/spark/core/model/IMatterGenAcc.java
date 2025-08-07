package com.mnrclara.spark.core.model;

public interface IMatterGenAcc {
	
	/*
	 * m.MATTER_TEXT AS matterNumber, m.BILL_MODE_ID AS billingMode, b.BILL_MODE_TEXT AS billingDesc, c.FIRST_LAST_NM AS clientName
	 */
	public String getMatterName();
	public String getBillingMode();
	public String getBillingDesc();
	public String getClientName();
	public Long getCategoryId();
	public Long getSubCategoryId();
	public Double getAdminCost();
	
}
