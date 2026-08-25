package com.tekclover.wms.api.idmaster.model.auditlog;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblauditlog")
public class AuditLog { 
	
	@Column(name = "C_ID")
	private String companyCode;
	
	@Column(name = "PLANT_ID")
	private String plantID;
	
	@Column(name = "WH_ID")
	private String warehouseId;
	
	@Id
	@Column(name = "AUD_LOG_NO")
	private String auditLogNumber;
	
	@Column(name = "FISCAL_YEAR")
	private Integer fiscalYear;
	
	@Column(name = "OBJ_NM")
	private String objectName;
	
	@Column(name = "SCREEN_NO")
	private Integer screenNo;
	
	@Column(name = "SUB_SCREEN_NO")
	private Integer subScreenNo;
	
	@Column(name = "TABLE_NM")
	private String tableName;
	
	@Column(name = "MOD_FIELD")
	private String modifiedField;
	
	@Column(name = "OLD_VL")
	private String oldValue;
	
	@Column(name = "NEW_VL")
	private String newValue;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator = 0L;

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
    private Date createdOn = new Date();

	@Column(name = "UTD_BY")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}
