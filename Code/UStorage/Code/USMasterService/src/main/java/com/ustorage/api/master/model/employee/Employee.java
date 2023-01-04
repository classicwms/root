package com.ustorage.api.master.model.employee;

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
@Table(name = "tblemployee")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_employee_code")
	@GenericGenerator(name = "seq_employee_code", strategy = "com.ustorage.api.master.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "EC"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%010d")})
	@Column(name = "EMPLOYEE_CODE")
	private String employeeCode;

	@Column(name = "CODE_ID")
	private String codeId;

	@Column(name = "EMPLOYEE_NAME")
	private String employeeName;
	
	@Column(name = "EMPLOYEE_SBU")
	private String employeeSbu;

	@Column(name = "EMPLOYEE_EMAIL")
	private String employeeEmail;

	@Column(name = "EMPLOYEE_PHONE")
	private String employeePhone;

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

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
	private Date createdOn;

	@Column(name = "UTD_BY")
	private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn;
}
