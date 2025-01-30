package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class BinLocation {

	private String storageBin;        // ST_BIN
	private String languageId;        // LANG_ID
	private String companyCodeId;    // C_ID
	private String plantId;            // PLANT_ID
	private String warehouseId;        // WH_ID
	private Long floorId;            // FL_ID
	private String storageSectionId;    // ST_SEC_ID
	private String rowId;            // ROW_ID
	private String aisleNumber;        // AISLE_ID
	private String spanId;            // SPAN_ID
	private String shelfId;            // SHELF_ID
	private String binSectionId;        // BIN_SECTION_ID
	private Long storageTypeId;        // ST_TYP_ID
	private Long binClassId;        // BIN_CL_ID
	private String description;        // ST_BIN_TEXT
	private String binBarcode;        // BIN_BAR
	private Long putawayBlock;    // PUTAWAY_BLOCK
	private Long pickingBlock;    // PICK_BLOCK
	private String blockReason;        // BLK_REASON
	private Long statusId;            // STATUS_ID
	private String occupiedVolume;
	private String occupiedWeight;
	private String occupiedQuantity;
	private String remainingVolume;
	private String remainingWeight;
	private String remainingQuantity;
	private String totalVolume;
	private String totalWeight;
	private String totalQuantity;
	private Long deletionIndicator;    // Is_deleted
	private String dType;
	private String createdBy;        // CTD_BY

	// V2 fields
	private Boolean capacityCheck;
	private Double allocatedVolume;

	/**
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param storageBin
	 * @param floorId
	 * @param storageSectionId
	 * @param rowId
	 * @param aisleNumber
	 * @param spanId
	 * @param shelfId
	 * @param binSectionId
	 * @param storageTypeId
	 * @param binClassId
	 * @param description
	 * @param binBarcode
	 * @param putawayBlock
	 * @param pickingBlock
	 * @param blockReason
	 * @param statusId
	 * @param occupiedVolume
	 * @param occupiedWeight
	 * @param occupiedQuantity
	 * @param remainingVolume
	 * @param remainingWeight
	 * @param remainingQuantity
	 * @param totalVolume
	 * @param totalWeight
	 * @param totalQuantity
	 * @param capacityCheck
	 * @param allocatedVolume
	 * @param deletionIndicator
	 * @param dType
	 * @param createdBy
	 */
	public BinLocation(String languageId, String companyCodeId, String plantId, String warehouseId, String storageBin, Long floorId,
					   String storageSectionId, String rowId, String aisleNumber, String spanId, String shelfId, String binSectionId,
					   Long storageTypeId, Long binClassId, String description, String binBarcode, Long putawayBlock,
					   Long pickingBlock, String blockReason, Long statusId, String occupiedVolume, String occupiedWeight,
					   String occupiedQuantity, String remainingVolume, String remainingWeight, String remainingQuantity,
					   String totalVolume, String totalWeight, String totalQuantity, Boolean capacityCheck, Double allocatedVolume,
					   Long deletionIndicator, String dType, String createdBy) {

		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.storageBin = storageBin;
		this.floorId = floorId;
		this.storageSectionId = storageSectionId;
		this.rowId = rowId;
		this.aisleNumber = aisleNumber;
		this.spanId = spanId;
		this.shelfId = shelfId;
		this.binSectionId = binSectionId;
		this.storageTypeId = storageTypeId;
		this.binClassId = binClassId;
		this.description = description;
		this.binBarcode = binBarcode;
		this.putawayBlock = putawayBlock;
		this.pickingBlock = pickingBlock;
		this.blockReason = blockReason;
		this.statusId = statusId;
		this.occupiedVolume = occupiedVolume;
		this.occupiedWeight = occupiedWeight;
		this.occupiedQuantity = occupiedQuantity;
		this.remainingVolume = remainingVolume;
		this.remainingWeight = remainingWeight;
		this.remainingQuantity = remainingQuantity;
		this.totalVolume = totalVolume;
		this.totalWeight = totalWeight;
		this.totalQuantity = totalQuantity;
		this.capacityCheck = capacityCheck;
		this.allocatedVolume = allocatedVolume;
		this.deletionIndicator = deletionIndicator;
		this.dType = dType;
		this.createdBy = createdBy;
	}
}
