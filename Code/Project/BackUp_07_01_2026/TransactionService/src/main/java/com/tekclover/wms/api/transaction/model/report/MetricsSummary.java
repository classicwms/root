package com.tekclover.wms.api.transaction.model.report;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmatricssummary")
public class MetricsSummary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long summaryId;

	private Long referenceId;
//	private Long metricsId;
	private Long totalOrder;
	private Double lineItems;
	private Double percentageShipped;
	private Double lineItemPicked;
	private Long orderedQty;
	private Long deliveryQty;
	private Double shippedLines;
}
