package com.tekclover.wms.core.model.idmaster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmailingreport",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_key_mailingreport",
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "LANG_ID", "FILE_NAME"})
		}
)
@IdClass(MailingReportCompositeKey.class)
public class MailingReport {

	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
	private String companyCodeId;

	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
	private String plantId;

	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
	private String warehouseId;

	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
	private String languageId;

	@Id
	@Column(name = "FILE_NAME", columnDefinition = "nvarchar(255)")
	private String fileName;

	@Column(name = "GROUP_BY", columnDefinition = "nvarchar(50)")
	private String groupBy;

	@Column(name = "REPORT_DATE", columnDefinition = "nvarchar(50)")
	private String reportDate;

	@Column(name = "IS_DELETED")
	private Long deletionIndicator;

	@Column(name = "MAIL_SENT")
	private String mailSent;

	@Column(name = "MAIL_SENT_FAILED")
	private String mailSentFailed;

	@Column(name = "UPLOADED")
	private Boolean uploaded;

}