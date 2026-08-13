package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class QualityHeaderStream {

	private String refDocNumber;
	private String partnerCode;
	private String qualityInspectionNo;
	private String actualHeNo;
	private Long statusId;
	private String qcToQty;
	private String manufacturerPartNo;
	private String referenceField1;
	private String referenceField2;
	private String referenceField4;
	private String qualityCreatedBy;
	private Date qualityCreatedOn;

	/**
	 * Quality Header
	 * @param refDocNumber
	 * @param partnerCode
	 * @param qualityInspectionNo
	 * @param actualHeNo
	 * @param statusId
	 * @param qcToQty
	 * @param manufacturerPartNo
	 * @param referenceField1
	 * @param referenceField2
	 * @param referenceField4
	 * @param qualityCreatedBy
	 * @param qualityCreatedOn
	 */
	public QualityHeaderStream(String refDocNumber,
                               String partnerCode,
                               String qualityInspectionNo,
                               String actualHeNo,
                               Long statusId,
                               String qcToQty,
                               String manufacturerPartNo,
                               String referenceField1,
                               String referenceField2,
                               String referenceField4,
							   String qualityCreatedBy,
                               Date qualityCreatedOn) {
		this.refDocNumber = refDocNumber;
		this.partnerCode = partnerCode;
		this.qualityInspectionNo = qualityInspectionNo;
		this.actualHeNo = actualHeNo;
		this.statusId = statusId;
		this.qcToQty = qcToQty;
		this.manufacturerPartNo = manufacturerPartNo;
		this.referenceField1 = referenceField1;
		this.referenceField2 = referenceField2;
		this.referenceField4 = referenceField4;
		this.qualityCreatedBy = qualityCreatedBy;
		this.qualityCreatedOn = qualityCreatedOn;
	}
}
