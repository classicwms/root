package com.tekclover.wms.api.idmaster.model.statusmessagesid;

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
 *  `MESSAGE_ID`, `LANG_ID`,`MESSAGE_TYPE`
 */
@Table(
		name = "tblstatusmessagesid",
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_statusmessagesid",
						columnNames = {"MESSAGE_ID", "LANG_ID", "MESSAGE_TYPE"})
				}
		)
@IdClass(StatusMessagesIdCompositeKey.class)
public class StatusMessagesId {
	
	@Id
	@Column(name = "MESSAGE_ID", columnDefinition = "nvarchar(10)")
	private String messageId;
		
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(5)") 
	private String languageId;

	@Id
	@Column(name = "MESSAGE_TYPE", columnDefinition = "nvarchar(50)")
	private String messageType;

	@Column(name = "MESSAGE_TEXT", columnDefinition = "nvarchar(500)")
	private String messageText;
	
	@Column(name = "IS_DELETED") 
	private Long deletionIndicator;

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
	
	@Column(name = "CTD_BY",columnDefinition = "nvarchar(50)")
	private String createdBy;

	@Column(name = "CTD_ON")
    private Date createdOn = new Date();

	@Column(name = "UTD_BY",columnDefinition = "nvarchar(50)")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();	
}
