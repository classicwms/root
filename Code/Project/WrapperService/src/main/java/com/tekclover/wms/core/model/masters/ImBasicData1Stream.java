package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.Date;

@Data
public class ImBasicData1Stream {

	private String uomId;
	private String warehouseId;
	private String itemCode;
	private String description;
	private String manufacturerPartNo;
	private Long itemType;
	private Long itemGroup;
	private String createdBy;
	private Date createdOn;

	/**
	 * ImBasicData
	 * @param uomId
	 * @param warehouseId
	 * @param itemCode
	 * @param description
	 * @param manufacturerPartNo
	 * @param itemType
	 * @param itemGroup
	 * @param createdBy
	 * @param createdOn
	 */
	public ImBasicData1Stream(String uomId,
                              String warehouseId,
                              String itemCode,
                              String description,
                              String manufacturerPartNo,
                              Long itemType,
                              Long itemGroup,
                              String createdBy,
                              Date createdOn) {
		this.uomId = uomId;
		this.warehouseId = warehouseId;
		this.itemCode = itemCode;
		this.description = description;
		this.manufacturerPartNo = manufacturerPartNo;
		this.itemType = itemType;
		this.itemGroup = itemGroup;
		this.createdBy = createdBy;
		this.createdOn = createdOn;
	}
}
