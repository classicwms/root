package com.tekclover.wms.core.batch.scheduler.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblinventory")
public class Inventory { 

	@Column(name = "wh_id") 
	private String warehouseId;
	
	@Column(name = "pack_barcode")
	private String packBarcodes;
	
	@Id
	@Column(name = "itm_code") 
	private String itemCode;
	
	@Column(name = "st_bin") 
	private String storageBin;
	
	@Column(name = "stck_typ_id") 
	private Long stockTypeId;
	
	@Column(name = "text") 
	private String description;
	
	@Column(name = "inv_qty") 
	private Double inventoryQuantity;
	
	@Column(name = "alloc_qty") 
	private Double allocatedQuantity;
	
	@Column(name = "inv_uom") 
	private String inventoryUom;
	
	@Column(name = "ref_field_8") 
	private String referenceField8;
	
	@Column(name = "ref_field_9") 
	private String referenceField9;
	
	@Column(name = "ref_field_10")
	private String referenceField10;

}
