package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class PutAwayHeaderStream {

	private String refDocNumber;
	private String packBarcodes;
	private String putAwayNumber;
	private String proposedStorageBin;
	private Double putAwayQuantity;
	private String proposedHandlingEquipment;
	private Long statusId;
	private String referenceField5;
	private String createdBy;
	private Date createdOn;

	/**
	 * GrHeader
	 * @param refDocNumber
	 * @param proposedStorageBin
	 * @param packBarcodes
	 * @param putAwayQuantity
	 * @param putAwayNumber
	 * @param proposedHandlingEquipment
	 * @param referenceField5
	 * @param statusId
	 * @param createdBy
	 * @param createdOn
	 */
	public PutAwayHeaderStream(String refDocNumber,
								String packBarcodes,
								String putAwayNumber,
								String proposedStorageBin,
								Double putAwayQuantity,
								String proposedHandlingEquipment,
								Long statusId,
								String referenceField5,
								String createdBy,
								Date createdOn) {
		this.refDocNumber = refDocNumber;
		this.packBarcodes = packBarcodes;
		this.putAwayNumber = putAwayNumber;
		this.proposedStorageBin = proposedStorageBin;
		this.putAwayQuantity = putAwayQuantity;
		this.proposedHandlingEquipment = proposedHandlingEquipment;
		this.statusId = statusId;
		this.referenceField5 = referenceField5;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}
}
