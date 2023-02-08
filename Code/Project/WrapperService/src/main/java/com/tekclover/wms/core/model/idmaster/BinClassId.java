package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;


@Data
public class BinClassId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long binClassId;
	private String languageId;
	private String binClass;
	private Long deletionIndicator;
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
	private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
	private Date updatedOn = new Date();	
}
