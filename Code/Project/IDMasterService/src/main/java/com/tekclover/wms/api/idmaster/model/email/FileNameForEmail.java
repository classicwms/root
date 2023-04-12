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
@Table(name = "tblfilenameforemail")
public class FileNameForEmail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="File_NAME_ID")
	private Long fileNameId;

	@Column(name = "DISPATCH_110", columnDefinition = "nvarchar(255)")
	private String dispatch110;

	@Column(name = "DELIVERY_110", columnDefinition = "nvarchar(255)")
	private String delivery110;

	@Column(name = "DISPATCH_111", columnDefinition = "nvarchar(255)")
	private String dispatch111;

	@Column(name = "DELIVERY_111", columnDefinition = "nvarchar(255)")
	private String delivery111;

	@Column(name = "GROUP_BY", columnDefinition = "nvarchar(50)")
	private String groupBy;

	@Column(name = "IS_DELETED")
	private Long deletionIndicator;

	@Column(name = "MAIL_SENT")
	private String mailSent;

	@Column(name = "MAIL_SENT_FAILED")
	private String mailSentFailed;

	@Column(name = "REPORT_DATE", columnDefinition = "nvarchar(50)")
	private String reportDate;
}
