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
