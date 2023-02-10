package com.ustorage.api.trans.model.workorder;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import com.ustorage.api.trans.model.itemservice.ItemService;
import com.ustorage.api.trans.sequence.BaseSequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblworkorder")
@Where(clause = "IS_DELETED='0'")
public class WorkOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_work_order")
	@GenericGenerator(name = "seq_work_order", strategy = "com.ustorage.api.trans.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "WO"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%010d")})
	@Column(name = "WORK_ORDER_ID")
	private String workOrderId;

	@Column(name = "CODE_ID")
	private String codeId;

	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@Column(name = "REMARKS")
	private String remarks;
			
	@Column(name = "CREATED")
	private String created;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "workOrderId",fetch = FetchType.EAGER)
	private Set<WoProcessedBy> woProcessedBy;
				
	@Column(name = "PROCESSED_TIME")
	private String processedTime;
	
	@Column(name = "LEAD_TIME")
	private String leadTime;

	@Column(name = "PLANNED_HOURS")
	private String plannedHours;
	
	@Column(name = "JOB_CARD")
	private String jobCard;

	@Column(name = "JOB_CARD_TYPE")
	private String jobCardType;
	
	@Column(name = "STATUS")
	private String status;

	@Column(name = "START_TIME")
	private String startTime;

	@Column(name = "END_TIME")
	private String endTime;

	@Column(name = "START_DATE")
	private Date startDate;

	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name = "WORK_ORDER_DATE")
	private Date workOrderDate;

	@Column(name = "WORK_ORDER_NUMBER")
	private String workOrderNumber;

	@Column(name = "WORK_ORDER_SBU")
	private String workOrderSbu;

	@Column(name = "FROM_ADDRESS")
	private String fromAddress;

	@Column(name = "TO_ADDRESS")
	private String toAddress;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "workOrderId",fetch = FetchType.EAGER)
	private Set<ItemService> itemServices;

	private String workOrderProcessedBy;

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
