package com.tekclover.wms.api.transaction.model.inbound.gr.v2;

import com.tekclover.wms.api.transaction.model.inbound.gr.GrHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class GrHeaderV2 extends GrHeader {



	@Column(name = "ACCEPT_QTY")
	private Double acceptedQuantity;

	@Column(name = "DAMAGE_QTY")
	private Double damagedQuantity;

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;

	@Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
	private String statusDescription;

	@Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
	private String middlewareId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

	@Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
	private String manufacturerFullName;

	@Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
	private String manufacturerName;

	@Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
	private String referenceDocumentType;

	@Column(name = "TRANSFER_ORDER_DATE")
	private Date transferOrderDate;

	@Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
	private String isCompleted;

	@Column(name = "IS_CANCELLED", columnDefinition = "nvarchar(20)")
	private String isCancelled;

	@Column(name = "M_UPDATED_ON")
	private Date mUpdatedOn;

	@Column(name = "SOURCE_BRANCH_CODE", columnDefinition = "nvarchar(50)")
	private String sourceBranchCode;

	@Column(name = "SOURCE_COMPANY_CODE", columnDefinition = "nvarchar(50)")
	private String sourceCompanyCode;

	@Column(name = "PARENT_PRODUCTION_ORDER_NO", columnDefinition = "nvarchar(50)")
	private String parentProductionOrderNo;

	@Column(name = "SAP_TYPE", columnDefinition = "nvarchar(50)")
	private String sapType;

	@Column(name = "SAP_FLAG", columnDefinition = "nvarchar(50)")
	private String sapFlag;

	@Column(name = "VEHICLE_NO", columnDefinition = "nvarchar(50)")
	private String vehicleNumber;

}
