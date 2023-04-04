package com.tekclover.wms.api.idmaster.model.currency;

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
@Table(name = "tblcurrencyid")
public class Currency { 
	
	@Id
	@Column(name = "CURR_ID")
	private Long currencyId;
	
	@Column(name = "CURR_TEXT")
	private String currencyDescription;

	@Column(name = "LANG_ID")
	private String languageId;

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
    private Date createdOn = new Date();

	@Column(name = "UTD_BY")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}
