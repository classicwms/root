package com.tekclover.wms.api.inbound.transaction.model.preoutbound;

import java.util.Date;
public interface PreOutboundHeaderStream {
	
	public String getLanguageId();
	public String getCompanyCodeId();
	public String getPlantId();
	public String getWarehouseId();
	public String getRefDocNumber();
	public String getPreOutboundNo();
	public String getPartnerCode();
	public Long getOutboundOrderTypeId();
	public String getReferenceDocumentType();
	public Long getStatusId();
	public Date getRefDocDate();
	public Date getRequiredDeliveryDate();
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
	public String getRemarks();
	public String getCreatedBy();
	public Date getCreatedOn();
	public String getUpdatedBy();
	public Date getUpdatedOn();
	public String getStatusDescription();
}
