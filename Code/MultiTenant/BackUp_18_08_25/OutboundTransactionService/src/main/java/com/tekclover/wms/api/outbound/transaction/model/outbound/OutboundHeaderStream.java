package com.tekclover.wms.api.outbound.transaction.model.outbound;

import java.util.Date;

//@Data
public interface OutboundHeaderStream {

	/*private String refDocNumber;
	private String partnerCode;
	private String referenceDocumentType;
	private Long statusId;
	private Date refDocDate;
	private Date requiredDeliveryDate;
	private String referenceField1;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private Date deliveryConfirmedOn;

	*//**
//	 * Outbound Header
//	 * @param refDocNumber
//	 * @param partnerCode
//	 * @param referenceDocumentType
//	 * @param statusId
//	 * @param refDocDate
//	 * @param requiredDeliveryDate
//	 * @param referenceField1
//	 * @param referenceField7
//	 * @param referenceField8
//	 * @param referenceField9
//	 * @param referenceField10
//	 * @param deliveryConfirmedOn
	 *//*
	public OutboundHeaderStream(String refDocNumber,
                                String partnerCode,
                                String referenceDocumentType,
                                Long statusId,
                                Date refDocDate,
                                Date requiredDeliveryDate,
								String referenceField1,
								String referenceField7,
								String referenceField8,
								String referenceField9,
								String referenceField10,
								Date deliveryConfirmedOn) {
		this.refDocNumber = refDocNumber;
		this.partnerCode = partnerCode;
		this.referenceDocumentType = referenceDocumentType;
		this.statusId = statusId;
		this.refDocDate = refDocDate;
		this.requiredDeliveryDate = requiredDeliveryDate;
		this.referenceField1 = referenceField1;
		this.referenceField7 = referenceField7;
		this.referenceField8 = referenceField8;
		this.referenceField9 = referenceField9;
		this.referenceField10 = referenceField10;
		this.deliveryConfirmedOn = deliveryConfirmedOn;
	}*/

	public String getLanguageId();
	public String getCompanyCodeId();
	public String getPlantId();
	public String getWarehouseId();
	public String getPreOutboundNo();
	public String getRefDocNumber();
	public String getPartnerCode();
	public String getDeliveryOrderNo();
	public String getReferenceDocumentType();
	public Long getOutboundOrderTypeId();
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
	public String getDeliveryConfirmedBy();
	public Date getDeliveryConfirmedOn();
	public String getUpdatedBy();
	public Date getUpdatedOn();
	public String getReversedBy();
	public Date getReversedOn();
	public String getStatusDescription();
}
