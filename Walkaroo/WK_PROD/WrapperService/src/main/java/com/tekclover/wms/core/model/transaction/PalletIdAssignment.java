package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class PalletIdAssignment {

	private Long paId;

	private String languageId;

	private String companyCodeId;

	private String plantId;

	private String warehouseId;

	private String preInboundNo;

	private String refDocNumber;

	private Long inboundOrderTypeId;

	private String palletCode;

	private String palletId;

	private String caseCode;

	private String packBarcodes;

	private String proposedStorageBin;

	private String assignedUserId;

	private Long statusId;

	private String storageSectionId;

	private String companyDescription;

	private String plantDescription;

	private String warehouseDescription;

	private String statusDescription;

	private String referenceDocumentType;

	private String levelId;

    private String materialNo;
    
    private String priceSegment;
    
    private String articleNo;
    
    private String gender;
    
    private String color;
    
    private String size;
    
    private String noPairs;

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

	private Date createdOn;

	private String confirmedBy;

	private Date confirmedOn;

	private String assignedBy;

	private Date assignedOn;

	private String updatedBy;

	private Date updatedOn;

}