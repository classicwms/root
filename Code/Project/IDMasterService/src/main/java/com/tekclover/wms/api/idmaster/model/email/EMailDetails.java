package com.tekclover.wms.api.idmaster.model.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblemail")
public class EMailDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private Long id;

//	@NotEmpty (message = "EMail cannot be blank.")
//	@Email (message = "Please correct EMail address.")
	@Column(name = "FROM_ADDRESS", columnDefinition = "nvarchar(50)")
	private String fromAddress;
	
	@NotEmpty (message = "EMail cannot be blank.")
	@Column(name = "TO_ADDRESS", columnDefinition = "nvarchar(50)")
	private String toAddress;
	
//	@Email (message = "Please correct EMail address.")
	@NotEmpty (message = "EMail cannot be blank.")
	@Column(name = "CC_ADDRESS", columnDefinition = "nvarchar(50)")
	private String ccAddress;
	
//	@NotEmpty (message = "Subject cannot be blank.")
	@Column(name = "SUBJECT", columnDefinition = "nvarchar(255)")
	private String subject;
	
//	@NotEmpty (message = "body text message cannot be blank.")
	@Column(name = "BODY_TEXT", columnDefinition = "nvarchar(1000)")
	private String bodyText;
	@Column(name = "GROUP_BY")
	private String groupBy;

	@Column(name = "IS_DELETED")
	private Long deletionIndicator;

//	private String uid = UUID.randomUUID().toString();
	@Column(name = "SENDER_NAME", columnDefinition = "nvarchar(255)")
	private String senderName;
	@Column(name = "MEETING_START_TIME")
	private Date meetingStartTime;
	@Column(name = "MEETING_END_TIME")
	private Date meetingEndTime;
}
