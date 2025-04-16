package com.tekclover.wms.core.model.idmaster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
public class HhtNotificationToken {

	private Long notificationId;

	private String languageId;

	private String companyId;

	private String plantId;

	private String warehouseId;

	private String deviceId;

	private String userId;

	private String tokenId;

	private boolean tokenStatus;
	private boolean loginStatus;


	private Long deletionIndicator;


	private Date createdOn = new Date();


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





}
