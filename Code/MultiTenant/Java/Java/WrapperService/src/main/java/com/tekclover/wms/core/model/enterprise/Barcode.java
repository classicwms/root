package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Barcode { 
	
	private Long barcodeTypeId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private String method;
	private Long barcodeSubTypeId;
	private Long levelId;
	private String levelReference;
	private Long processId;
	private String languageId;
	private String numberRangeFrom;
	private String numberRangeTo;
	private Double barcodeLength;
	private Double barcodeWidth;
	private String dimensionUom;
	private String labelInformation;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
