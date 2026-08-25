package com.tekclover.wms.api.inbound.orders.model.hhtnotification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`DEVICE_ID`,USR_ID`,`TOKEN_ID`
 */
@Table(name = "tblhhtnotificationtoken")

public class HhtNotificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NOTIFICATION_ID")
	private Long notificationId;

	@Column(name = "NOTIFICATION_HEADER_ID")
	private Long notificationHeaderId;

	@Column(name = "LANG_ID",columnDefinition = "nvarchar(5)")
	private String languageId;

	@Column(name = "C_ID",columnDefinition = "nvarchar(50)")
	private String companyId;

	@Column(name = "PLANT_ID",columnDefinition = "nvarchar(50)")
	private String plantId;

	@Column(name = "WH_ID",columnDefinition = "nvarchar(50)")
	private String warehouseId;

	@Column(name = "DEVICE_ID",columnDefinition = "nvarchar(50)")
	private String deviceId;

	@Column(name = "USR_ID",columnDefinition = "nvarchar(50)")
	private String userId;

	@Column(name = "TOKEN_ID",columnDefinition = "nvarchar(200)")
	private String tokenId;

	@Column(name = "TOKEN_STATUS")
	private boolean tokenStatus;

	@Column(name = "LOGIN_STATUS")
	private boolean loginStatus;

	@Column(name = "IS_DELETED")
	private Long deletionIndicator;

	@Column(name = "CTD_BY",columnDefinition = "nvarchar(50)")
	private String createdBy;

	@Column(name = "CTD_ON")
	private Date createdOn = new Date();

	@Column(name = "UTD_BY",columnDefinition = "nvarchar(50)")
	private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn;

	@Column(name = "REF_FIELD_1",columnDefinition = "nvarchar(200)")
	private String referenceField1;

	@Column(name = "REF_FIELD_2",columnDefinition = "nvarchar(200)")
	private String referenceField2;

	@Column(name = "REF_FIELD_3",columnDefinition = "nvarchar(200)")
	private String referenceField3;

	@Column(name = "REF_FIELD_4",columnDefinition = "nvarchar(200)")
	private String referenceField4;

	@Column(name = "REF_FIELD_5",columnDefinition = "nvarchar(200)")
	private String referenceField5;

	@Column(name = "REF_FIELD_6",columnDefinition = "nvarchar(200)")
	private String referenceField6;

	@Column(name = "REF_FIELD_7",columnDefinition = "nvarchar(200)")
	private String referenceField7;

	@Column(name = "REF_FIELD_8",columnDefinition = "nvarchar(200)")
	private String referenceField8;

	@Column(name = "REF_FIELD_9",columnDefinition = "nvarchar(200)")
	private String referenceField9;

	@Column(name = "REF_FIELD_10",columnDefinition = "nvarchar(200)")
	private String referenceField10;
}