package com.tekclover.wms.api.masters.model.imbasicdata1;

import lombok.Data;

@Data
public class UpdateImBasicData1 {

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
	private Long itemType;
	private Long itemGroup;
	private Long subItemGroup;
	private String storageSectionId;
	private Boolean capacityCheck;
	private Double totalStock;
	private Double minimumStock;
	private Double maximumStock;
	private Double reorderLevel;
	private Double replenishmentQty;
	private Double safetyStock;
	private String capacityUnit;
	private String capacityUom;
	private String quantity;
	private Double weight;
	private Long statusId;
	private Boolean shelfLifeIndicator;
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
	private Long deletionIndicator;
	private String updatedBy;

	private Double length;
	private Double width;
	private Double height;
	private String dimensionUom;
	private Double volume;

}
