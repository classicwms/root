package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.Date;

@Data
public class StorageBinStream {

	private String storageBin;
	private String warehouseId;
	private Long floorId;
	private String storageSectionId;
	private String spanId;
	private Long statusId;
	private String createdBy;
	private Date createdOn;

	/**
	 * Storage Bin
	 * @param storageBin
	 * @param warehouseId
	 * @param floorId
	 * @param storageSectionId
	 * @param spanId
	 * @param statusId
	 * @param createdBy
	 * @param createdOn
	 */
	public StorageBinStream(String storageBin,
                            String warehouseId,
                            Long floorId,
                            String storageSectionId,
                            String spanId,
                            Long statusId,
                            String createdBy,
                            Date createdOn) {
		this.storageBin = storageBin;
		this.warehouseId = warehouseId;
		this.floorId = floorId;
		this.storageSectionId = storageSectionId;
		this.spanId = spanId;
		this.statusId = statusId;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}
}
