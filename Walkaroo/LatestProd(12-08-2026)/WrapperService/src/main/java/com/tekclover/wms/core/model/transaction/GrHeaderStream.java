package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class GrHeaderStream {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preInboundNo;
	private String refDocNumber;
	private String stagingNo;
	private String goodsReceiptNo;
	private String palletCode;
	private String caseCode;
	private Long inboundOrderTypeId;
	private Long statusId;
	private String grMethod;
	private String containerReceiptNo;
	private String dockAllocationNo;
	private String containerNo;
	private String vechicleNo;
	private Date expectedArrivalDate;
	private Date goodsReceiptDate;
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
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
	private String confirmedBy;
	private Date confirmedOn;

	/**
	 * GrHeader
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param preInboundNo
	 * @param refDocNumber
	 * @param stagingNo
	 * @param goodsReceiptNo
	 * @param palletCode
	 * @param caseCode
	 * @param inboundOrderTypeId
	 * @param statusId
	 * @param grMethod
	 * @param containerReceiptNo
	 * @param dockAllocationNo
	 * @param containerNo
	 * @param vechicleNo
	 * @param expectedArrivalDate
	 * @param goodsReceiptDate
	 * @param deletionIndicator
	 * @param referenceField1
	 * @param referenceField2
	 * @param referenceField3
	 * @param referenceField4
	 * @param referenceField5
	 * @param referenceField6
	 * @param referenceField7
	 * @param referenceField8
	 * @param referenceField9
	 * @param referenceField10
	 * @param createdBy
	 * @param createdOn
	 * @param updatedBy
	 * @param updatedOn
	 * @param confirmedBy
	 * @param confirmedOn
	 */
	public GrHeaderStream(	String languageId,
							String companyCodeId,
							String plantId,
							String warehouseId,
							String preInboundNo,
							String refDocNumber,
							String stagingNo,
							String goodsReceiptNo,
							String palletCode,
							String caseCode,
							Long inboundOrderTypeId,
							Long statusId,
							String grMethod,
							String containerReceiptNo,
							String dockAllocationNo,
							String containerNo,
							String vechicleNo,
							Date expectedArrivalDate,
							Date goodsReceiptDate,
							Long deletionIndicator,
							String referenceField1,
							String referenceField2,
							String referenceField3,
							String referenceField4,
							String referenceField5,
							String referenceField6,
							String referenceField7,
							String referenceField8,
							String referenceField9,
							String referenceField10,
							String createdBy,
							Date createdOn,
							String updatedBy,
							Date updatedOn,
							String confirmedBy,
							Date confirmedOn) {
		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.preInboundNo = preInboundNo;
		this.refDocNumber = refDocNumber;
		this.stagingNo = stagingNo;
		this.goodsReceiptNo = goodsReceiptNo;
		this.palletCode = palletCode;
		this.caseCode = caseCode;
		this.inboundOrderTypeId = inboundOrderTypeId;
		this.statusId = statusId;
		this.grMethod = grMethod;
		this.containerReceiptNo = containerReceiptNo;
		this.dockAllocationNo = dockAllocationNo;
		this.containerNo = containerNo;
		this.vechicleNo = vechicleNo;
		this.expectedArrivalDate = expectedArrivalDate;
		this.goodsReceiptDate = goodsReceiptDate;
		this.deletionIndicator = deletionIndicator;
		this.referenceField1 = referenceField1;
		this.referenceField2 = referenceField2;
		this.referenceField3 = referenceField3;
		this.referenceField4 = referenceField4;
		this.referenceField5 = referenceField5;
		this.referenceField6 = referenceField6;
		this.referenceField7 = referenceField7;
		this.referenceField8 = referenceField8;
		this.referenceField9 = referenceField9;
		this.referenceField10 = referenceField10;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
		this.updatedBy = updatedBy;
		this.updatedOn = updatedOn;
		this.confirmedBy = confirmedBy;
		this.confirmedOn = confirmedOn;
	}
}
