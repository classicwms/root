package com.iweb2b.core.model.integration;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Set;

@Data
public class UpdateSoftDataUpload {


    private String actionType;
    private String consignmentType;
    private String movementType;
    private String ewayBill;
    private String loadType;
    private String description;
    private String customerCode;
    private String referenceNumber;
    private String serviceTypeId;
    private String codFavorOf;
    private String codCollectionMode;
    private String dimensionUnit;
    private String length;
    private String width;
    private String height;
    private String weightUnit;
    private String volume;
    private String volumeUnit;
    private String weight;
    private String codAmount;
    private String invoiceAmount;
    private String invoiceNumber;
    private String invoiceDate;
    private String declaredValue;
    private String declaredValueWithoutTax;
    private String productCode;
    private String numPieces;
    private String customerReferenceNumber;
    private String isRiskSurchargeApplicable;
    private String courierPartner;
    private String courierAccount;
    private String courierPartnerReferenceNumber;
    private String hubCode;
    private String hsnCode;

    private String reportingBranchCode;
    private String manifestNumber;
    private String manifestTime;
    private String autoAccept;
    private String constraintTag;
    private String workerCode;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "softDataUploadId",fetch = FetchType.EAGER)
    private Set<UpdateTaxDetail> tdetails;

    private String deliveryTimeSlotStart;
    private String deliveryTimeSlotEnd;
    private String scheduledAt;
    private Date serviceTime;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "softDataUploadId",fetch = FetchType.EAGER)
    private Set<UpdateOriginDetail> odetails;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "softDataUploadId",fetch = FetchType.EAGER)
    private Set<UpdateDestinationDetail> ddetails;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "softDataUploadId",fetch = FetchType.EAGER)
    private Set<UpdateNode> nodes;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "softDataUploadId",fetch = FetchType.EAGER)
    private Set<UpdateReturnDetail> rdetails;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "softDataUploadId",fetch = FetchType.EAGER)
    private Set<UpdatePiecesDetail> pdetails;

    private Long deletionIndicator;

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

    private String updatedBy;
}
