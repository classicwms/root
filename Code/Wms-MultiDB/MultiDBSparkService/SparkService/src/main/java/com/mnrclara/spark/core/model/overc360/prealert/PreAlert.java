package com.mnrclara.spark.core.model.overc360.prealert;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PreAlert {

    private String companyId;

    private String languageId;

    private String partnerId;

    private String masterAirwayBill;

    private String partnerHouseAirwayBill;

    private String partnerMasterAirwayBill;

    private String partnerName;

    private Double totalWeight;

    private String flightNo;

    private Long consoleIndicator;

    private Double consignmentValueLocal;

    private Long manifestIndicator;

    private String flightName;

    private Timestamp estimatedTimeOfDeparture;

    private Timestamp estimatedTimeOfArrival;

    private String noOfPieces;

    private Double consignmentValue;

    private Double exchangeRate;

    private Double iata;

    private Double customsInsurance;

    private Double duty;

    private Double addIata;

    private Double addInsurance;

    private Double customsValue;

    private Double calculatedTotalDuty;

    private String bayanHv;

    private String consigneePhoneNo;

    private String currency;

    private String description;

    private String consigneeName;

    private String shipper;

    private String origin;

    private String originCode;

//    private String consignmentValueKd;

    private String hsCode;

    private String partnerType;

    private String incoTerm;

    private String hawbType;

    private String hawbTypeId;

    private String hawbTypeDescription;

    private String companyName;

    private String languageDescription;

    private Timestamp hawbTimeStamp;

    private String consignmentLocalId;

    private String houseAirwayBill;

    private String hubCode;

    private String hubName;

    private String addDestinationDetails;

    private String addOriginDetails;

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

    private Timestamp createdOn;

    private String updatedBy;

    private Timestamp updatedOn;

    private Long countHawb;

    private Double totalValueShipment;
    private Double totalCostPerShipment;
    private Double approvals;
    private Double specialApprovals;
    private Double customDuty;
    private Double others;
    private Double otherCharges;
    private Double labours;
    private Double handlingFees;
    private Double clearanceCharge;
    private Double stampCharges;
    private Double handlingFork;
    private Double global;
    private Double nasDelivery;

    private String subCustomerName;
    private String subCustomerId;
    private String invoice;
    private String originFlightCountry;
    private String ddpInvoiceNo;
}