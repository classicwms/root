package com.tekclover.wms.core.model.idmaster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
public class HhtNotification {



	private String languageId;


	private String companyId;


	private String plantId;


	private String warehouseId;


	private String deviceId;

	private String userId;


	private String tokenId;


//	private List<HhtNotificationToken> hhtNotificationTokens;

	private Long deletionIndicator;

	private String createdBy;

	private Date createdOn = new Date();

	private String updatedBy;

	private Date updatedOn;



}
