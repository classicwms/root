package com.mnrclara.spark.core.model.b2b;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class Consignment {
        private Long consignmentId;
        private String referenceNumber;
        private String codAmount;
        private String codCollectionMode;
        private String customerCode;
        private String serviceTypeId;
        private String consignmentType;
        private String loadType;
        private String description;
        private String codFavorOf;
        private String dimensionUnit;
        private String length;
        private String width;
        private String height;
        private String weightUnit;
        private Double weight;
        private Double declaredValue;
        private Long numPieces;
        private String notes;
        private String customerReferenceNumber;
        private Boolean isRiskSurchargeApplicable;
        private Timestamp createdAt;
        private String statusDescription;
        private String customerCivilId;
        private String receiverCivilId;
        private String currency;
        private String awb3rdParty;
        private String scanType3rdParty;
        private String hubCode3rdParty;
        private String orderType;
        private String jntPushStatus;
        private String boutiqaatPushStatus;
        private Boolean isAwbPrinted;
        private String scanType;
        private String hubCode;
}
