package com.tekclover.wms.core.model.masters;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class ImBasicData2 { 
	
	private String languageId;
	
	private String companyCodeId;
	
	private String plantId;
	
	private String warehouseId;
	
	private String itemCode;
	
	private String itemBarcode;
	
	private Double length;
	
	private Double width;
	
	private Double height;
	
	private String dimensionUom;
	
	private Double volume;
	
	private String volumeUom;	
	
	private Double qpc20Ft;
	
	private Double qpc40Ft;
	
	private Double unitPrice;
	
	private Long currencyId;
	
	private Double grossWeight;
	
	private Double netWeight;
	
	private String weightUom;
	
	private String preferredStorageBin;
	
	private Object photo;

	private String itemClass;
	
	private Boolean crossDockingIndicator;
	
	private Long statusId;
	
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
	
	private Long deletionIndicator = 0L;
	
	private String createdby;	
	
	private Date createdon;
	
	private String updatedby;
	
	private Date updatedon;

}
