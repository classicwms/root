package com.tekclover.wms.api.enterprise.model.storagebintype;

import lombok.Data;

@Data
public class AddStorageBinType {

	private Long storageBinTypeId;
	
    private String companyId;
	
	private String plantId;
    
    private String warehouseId;
	
	private Long storageTypeId;
	
	private String languageId;
	
	private Double length;
	
	private Double width;
	
	private Double height;
	
	private String dimentionUom;
	
	private Double totalVolume;
	
	private String volumeUom;
	
	private Short storageBinTypeBlock;
	
	private String createdBy;
}
