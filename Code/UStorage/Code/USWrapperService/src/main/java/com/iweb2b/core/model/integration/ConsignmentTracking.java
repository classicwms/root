package com.iweb2b.core.model.integration;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class ConsignmentTracking {

	private Long id;
	private Long consignmentTrackingId;
	private String referenceNumber;
	private String serviceTypeId;
	private String courierPartnerReferenceNumber;
	private String courierPartner;
	private String courierAccount;
	private String attemptCount;
	private String status;
	private String deliveryKycType;
	private String deliveryKycNumber;
	private Boolean isCod;
	private String codAmount;
	private String hubCode;
	private String creationDate;
	private String weight;
	private String receiverName;
	private String numPieces;
	private String customerCode;
	private String customerName;
	private String billingType;
	private String originLocationCode;
	private String destinationLocationCode;
	private String drsNumber;
	private String pickupAttemptCount;
	private String carrierPaymentDetails;

	private Set<PiecesDetail> pdetails;
	private Set<OriginDetail> odetails;
	private Set<DestinationDetail> ddetails;
	private Set<Event> events;

	private Long deletionIndicator = 0L;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
