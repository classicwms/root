package com.tekclover.wms.api.masters.model.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblemail")
public class EMailDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="EMAIL_ID")
	private Long emailId;

	@Column(name = "C_ID",columnDefinition = "nvarchar(50)")
	private String companyCodeId;

	@Column(name = "PLANT_ID",columnDefinition = "nvarchar(50)")
	private String plantId;

	@Column(name = "WH_ID",columnDefinition = "nvarchar(50)")
	private String warehouseId;

	@Column(name = "LANG_ID",columnDefinition = "nvarchar(25)")
	private String languageId;

	@Column(name = "FROM_ADDRESS", columnDefinition = "nvarchar(50)")
	private String fromAddress;
	
	@Column(name = "TO_ADDRESS", columnDefinition = "nvarchar(100)")
	private String toAddress;
	
	@Column(name = "CC_ADDRESS", columnDefinition = "nvarchar(100)")
	private String ccAddress;
	
	@Column(name = "SUBJECT", columnDefinition = "nvarchar(500)")
	private String subject;
	
	@Column(name = "BODY_TEXT", columnDefinition = "nvarchar(2000)")
	private String bodyText;

	@Column(name = "GROUP_BY", columnDefinition = "nvarchar(50)")
	private String groupBy;

	@Column(name = "IS_DELETED")
	private Long deletionIndicator;

	@Column(name = "SENDER_NAME", columnDefinition = "nvarchar(255)")
	private String senderName;

	@Column(name = "MEETING_START_TIME")
	private Date meetingStartTime;

	@Column(name = "MEETING_END_TIME")
	private Date meetingEndTime;

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
	private Date createdOn;

	@Column(name = "UTD_BY")
	private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn;
}