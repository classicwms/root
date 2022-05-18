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
public class Warehouse { 
	
	private String warehouseId;
	private String companyId;
	private String plantId;
	private String modeOfImplementation;
	private Long warehouseTypeId;
	private String languageId;
	private Boolean inboundQACheck;
	private Boolean outboundQACheck;
	private String zone;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private Long zipCode;
	private String contactName;
	private String designation;
	private String phoneNumber;
	private String eMail;
	private Double length;
	private Double width;
	private Double totalArea;
	private String uom;
	private Long noAisles;
	private Double latitude;
	private Double longitude;
	private String storageMethod;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
