package com.tekclover.wms.core.model.idmaster;
import lombok.Data;
import java.util.Date;

@Data
public class Vertical {
	private Long verticalId;
	private String verticalName;
	private String languageId;
	private String remark;
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
	private String UpdatedBy;
	private Date updatedOn;
}
