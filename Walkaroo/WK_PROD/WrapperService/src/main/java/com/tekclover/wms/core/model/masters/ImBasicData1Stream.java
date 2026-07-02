package com.tekclover.wms.core.model.masters;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class ImBasicData1Stream {

	private String uomId;
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String description;
	private String model;
	private String specifications1;
	private String specifications2;
	private String eanUpcNo;
	private String manufacturerPartNo;
	private String hsnCode;
	private Long   itemType;
	private Long   itemGroup;
	private Long   subItemGroup;
	private String storageSectionId;
	private Double totalStock;
	private Double minimumStock;
	private Double maximumStock;
	private Double reorderLevel;
	private Double replenishmentQty;
	private Double safetyStock;
	private Long   statusId;
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
	private Long   deletionIndicator;
	private String createdBy;
	private Date   createdOn;
	private String updatedBy;
	private Date   updatedOn;

	/**
	 * ImBasicData
	 * @param uomId
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param itemCode
	 * @param description
	 * @param model
	 * @param specifications1
	 * @param specifications2
	 * @param eanUpcNo
	 * @param manufacturerPartNo
	 * @param hsnCode
	 * @param itemType
	 * @param itemGroup
	 * @param subItemGroup
	 * @param storageSectionId
	 * @param totalStock
	 * @param minimumStock
	 * @param maximumStock
	 * @param reorderLevel
	 * @param replenishmentQty
	 * @param safetyStock
	 * @param statusId
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
	 * @param deletionIndicator
	 * @param createdBy
	 * @param createdOn
	 * @param updatedBy
	 * @param updatedOn
	 */
	public ImBasicData1Stream(	String uomId,
								String languageId,
								String companyCodeId,
								String plantId,
								String warehouseId,
								String itemCode,
								String description,
								String model,
								String specifications1,
								String specifications2,
								String eanUpcNo,
								String manufacturerPartNo,
								String hsnCode,
								Long   itemType,
								Long   itemGroup,
								Long   subItemGroup,
								String storageSectionId,
								Double totalStock,
								Double minimumStock,
								Double maximumStock,
								Double reorderLevel,
								Double replenishmentQty,
								Double safetyStock,
								Long   statusId,
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
								Long   deletionIndicator,
								String createdBy,
								Date   createdOn,
								String updatedBy,
								Date   updatedOn
								) {
		this.uomId				= uomId;
		this.languageId			= languageId;
		this.companyCodeId		= companyCodeId;
		this.plantId			= plantId;
		this.warehouseId 		= warehouseId;
		this.itemCode			= itemCode;
		this.description		= description;
		this.model				= model;
		this.specifications1	= specifications1;
		this.specifications2	= specifications2;
		this.eanUpcNo			= eanUpcNo;
		this.manufacturerPartNo	= manufacturerPartNo;
		this.hsnCode			= hsnCode;
		this.itemType 			= itemType;
		this.itemGroup			= itemGroup;
		this.subItemGroup		= subItemGroup;
		this.storageSectionId	= storageSectionId;
		this.totalStock			= totalStock;
		this.minimumStock		= minimumStock;
		this.maximumStock		= maximumStock;
		this.reorderLevel		= reorderLevel;
		this.replenishmentQty	= replenishmentQty;
		this.safetyStock 		= safetyStock;
		this.statusId			= statusId;
		this.referenceField1	= referenceField1;
		this.referenceField2	= referenceField2;
		this.referenceField3	= referenceField3;
		this.referenceField4	= referenceField4;
		this.referenceField5	= referenceField5;
		this.referenceField6	= referenceField6;
		this.referenceField7	= referenceField7;
		this.referenceField8 	= referenceField8;
		this.referenceField9	= referenceField9;
		this.referenceField10	= referenceField10;
		this.deletionIndicator	= deletionIndicator;
		this.createdBy			= createdBy;
		this.createdOn			= createdOn;
		this.updatedBy			= updatedBy;
		this.updatedOn			= updatedOn;
	}
}
