package com.tekclover.wms.api.idmaster.model.vertical;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblverticalid")
public class Vertical { 
	  
	@Id
	@Column(name = "VERT_ID")
	private Long verticalId;
	
	@Column(name = "VERTICAL")
	private String verticalName;
	
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Column(name = "REMARK")
	private String remark;
	@Column(name = "IS_DELETED")
	private Long deletionIndicator;
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

	@Column(name = "REF_FIELD_6")
	private String referenceField6;

	@Column(name = "REF_FIELD_7")
	private String referenceField7;

	@Column(name = "REF_FIELD_8")
	private String referenceField8;

	@Column(name = "REF_FIELD_9")
	private String referenceField9;

	@Column(name = "REF_FIELD_10")
	private String referenceField10;
	@JsonIgnore
	@Column(name = "CTD_BY")
	private String createdBy;

	@JsonIgnore
	@Column(name = "CTD_ON")
    private Date createdOn = new Date();

	@JsonIgnore
	@Column(name = "UTD_BY")
    private String UpdatedBy;

	@JsonIgnore
	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}
