package com.tekclover.wms.api.outbound.transaction.model.auditlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltransactionauditlog")
public class AuditLog {

	@Column(name = "AUD_FILE_NO")
	private String auditFileNumber;

	@Column(name = "C_ID")
	private String companyCode;
	
	@Column(name = "PLANT_ID")
	private String plantID;
	
	@Column(name = "WH_ID")
	private String warehouseId;
	
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AUD_LOG_NO")
	private Long auditLogNumber;
	
	@Column(name = "FISCAL_YEAR")
	private Long fiscalYear;
	
	@Column(name = "OBJ_NM")
	private String objectName;
	
	@Column(name = "SCREEN_NO")
	private Long screenNo;
	
	@Column(name = "SUB_SCREEN_NO")
	private Long subScreenNo;
	
	@Column(name = "TABLE_NM")
	private String tableName;
	
	@Column(name = "MOD_FIELD")
	private String modifiedField;
	
	@Column(name = "OLD_VL")
	private String oldValue;
	
	@Column(name = "NEW_VL")
	private String newValue;

	@Column(name = "REF_DOC_NO")
	private String refDocNumber;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator = 0L;

	@Column(name = "REF_FIELD_1")
	private String referenceField1;

	@Column(name = "REF_FIELD_2")
	private String referenceField2;

	@Column(name = "REF_FIELD_3")
	private String referenceField3;

	@Column(name = "REF_FIELD_4")
	private String referenceField4;

	@Column(name = "REF_FIELD_5")
	private String referenceField5;

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
    private Date createdOn;

	@Column(name = "UTD_BY")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn;
}
