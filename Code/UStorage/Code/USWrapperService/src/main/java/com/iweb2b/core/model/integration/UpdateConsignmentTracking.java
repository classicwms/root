package com.iweb2b.core.model.integration;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Set;

@Data
public class UpdateConsignmentTracking {

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

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "consignmentTrackingId",fetch = FetchType.EAGER)
    private Set<UpdatePiecesDetail> pdetails;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "consignmentTrackingId",fetch = FetchType.EAGER)
    private Set<UpdateOriginDetail> odetails;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "consignmentTrackingId",fetch = FetchType.EAGER)
    private Set<UpdateDestinationDetail> ddetails;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "consignmentTrackingId",fetch = FetchType.EAGER)
    private Set<UpdateEvent> events;

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