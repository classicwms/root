package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class BinLocation { 
	/*
	 * 																					
	 */
	private String languageId;		// LANG_ID
	private String companyCodeId;	// C_ID
	private String plantId;			// PLANT_ID
	private String warehouseId;		// WH_ID
	private String storageBin;		// ST_BIN
	private Long floorId;			// FL_ID
	private String storageSectionId;	// ST_SEC_ID
	private String rowId;			// ROW_ID
	private String aisleNumber;		// AISLE_ID
	private String spanId;			// SPAN_ID
	private String shelfId;			// SHELF_ID
	private Long binSectionId;		// BIN_SECTION_ID
	private Long storageTypeId;		// ST_TYP_ID
	private Long binClassId;		// BIN_CL_ID
	private String description;		// ST_BIN_TEXT
	private String binBarcode;		// BIN_BAR
	private Boolean putawayBlock;	// PUTAWAY_BLOCK
	private Boolean pickingBlock;	// PICK_BLOCK
	private String blockReason;		// BLK_REASON
	private Long statusId;			// STATUS_ID
	private Long deletionIndicator;	// Is_deleted
	private String createdBy;		// CTD_BY
	
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
	* @param deletionIndicator
	* @param createdBy
	*/
	public BinLocation (String languageId, String companyCodeId, String plantId, String warehouseId, String storageBin, Long floorId, 
						String storageSectionId, String rowId, String aisleNumber, String spanId, String shelfId, Long binSectionId, 
					   Long storageTypeId, Long binClassId, String description,	String binBarcode, Boolean putawayBlock, 
					   Boolean pickingBlock, String blockReason, Long statusId, Long deletionIndicator, String createdBy) {
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
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
	}
}
