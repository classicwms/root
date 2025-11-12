package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblsummarymetrics")
public class SummaryMetrics {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long metricsId;

	private Long referenceId;
	private String partnerCode;
	private String type;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "summary_id")
	private MetricsSummary metricsSummary;
}
