package com.mnrclara.spark.core.model;

import lombok.Data;
import java.util.Date;

@Data
public class MatterGenAcc {

	private String matterNumber;

	private String languageId;

	private Long classId;

	private String caseInformationNo;

	private String clientId;

	private Long transactionId;

	private Long caseCategoryId;

	private Long caseSubCategoryId;

	private String matterDescription;

	private String fileNumber;

	private Long caseFileNumber;

	private Date caseOpenedDate;

	private Date caseClosedDate;

	private Date caseFiledDate;

	private Date priorityDate;

	private String receiptNoticeNo;

	private Date receiptDate;

	private Date expirationDate;

	private Date courtDate;

	private Date approvalDate;

	private String billingModeId;

	private String billingFrequencyId;

	private String billingFormatId;

	private String billingRemarks;

	private String arAccountNumber;

	private String trustDepositNo;

	private Double flatFeeAmount;

	private Double administrativeCost;

	private Double contigencyFeeAmount;

	private String rateUnit;

	private String directPhoneNumber;

	private Long statusId;

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

	private String referenceField11;

	private String referenceField12;

	private String referenceField13;

	private String referenceField14;

	private String referenceField15;

	private String referenceField16;

	private String referenceField17;

	private String referenceField18;

	private String referenceField19;

	private String referenceField20;

	private String createdBy;

	private Date createdOn;

	private String updatedBy;

	private Date updatedOn;
}
