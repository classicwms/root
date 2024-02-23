package com.tekclover.wms.api.idmaster.model.hhtnotification;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`DEVICE_ID`,USR_ID`,`TOKEN_ID`
 */
@Table(name = "tblhhtnotification")
public class HhtNotification {

	@Id
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

	@Column(name = "DEVICE_ID",columnDefinition = "nvarchar(500)")
	private String deviceId;

	@Column(name = "USR_ID",columnDefinition = "nvarchar(50)")
	private String userId;

	@Column(name = "TOKEN_ID",columnDefinition = "nvarchar(500)")
	private String tokenId;

//	@OneToMany(cascade = CascadeType.ALL, mappedBy = "notificationHeaderId", fetch = FetchType.EAGER)
//	private List<HhtNotificationToken> hhtNotificationTokens;

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

}
