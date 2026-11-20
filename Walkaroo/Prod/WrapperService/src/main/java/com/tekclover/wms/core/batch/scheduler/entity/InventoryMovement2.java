package com.tekclover.wms.core.batch.scheduler.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class InventoryMovement2 {
	private String warehouseId;
	private Long movementType;
	private Long submovementType;
	private String packBarcodes;
	private String itemCode;
	private String storageBin;
	private String storageMethod;
	private String description;
	private Double movementQty;
	private String inventoryUom;
	private String refDocNumber;
	private Date createdOn;
}
