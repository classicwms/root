package com.tekclover.wms.api.idmaster.model.interimbarcode;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblinterimbarcodeid")
public class InterimBarcode { 
	
	/*
	 * Storage Bin(String) , Item code(String) and Barcode(string) 
	 */
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INTERIM_BARCODE_ID")
    private Long interimBarcodeId = 0L;
	
	@Column(name = "ST_BIN", columnDefinition = "nvarchar(50)")
	private String storageBin;

	@Column(name = "ITM_CODE", columnDefinition = "nvarchar(50)")
	private String itemCode;

	@Column(name = "BARCODE", columnDefinition = "nvarchar(50)")
	private String barcode;

	@Column(name = "IS_DELETED")
	private Long deletionIndicator = 0L;

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
    private Date createdOn;

	@Column(name = "UTD_BY", columnDefinition = "nvarchar(50)")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn;
}
