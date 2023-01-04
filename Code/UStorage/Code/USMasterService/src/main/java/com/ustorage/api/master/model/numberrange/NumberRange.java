package com.ustorage.api.master.model.numberrange;

import java.util.Date;

import javax.persistence.*;


import com.ustorage.api.master.sequence.BaseSequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

@Table(	name = "tblnumberrange")
public class NumberRange {

	@Id
/*	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_number_range")
	@GenericGenerator(name = "seq_number_range", strategy = "com.ustorage.api.master.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "NR"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%010d")})*/
	@Column(name = "NUM_RAN_CODE")
	private Long numberRangeCode;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "DOCUMENT_CODE")
	private String documentCode;

	@Column(name = "DOCUMENT_NAME")
	private String documentName;
	
	@Column(name = "NUM_RAN_FROM")
	private Long numberRangeFrom;
	
	@Column(name = "NUM_RAN_TO")
	private Long numberRangeTo;
	
	@Column(name = "NUM_RAN_CURRENT")
	private String numberRangeCurrent;
	
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
