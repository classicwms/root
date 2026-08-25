package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@ToString(callSuper = true)
public class PeriodicHeaderV2 extends PeriodicHeader {

	private String companyDescription;

	private String plantDescription;

	private String warehouseDescription;

	private String statusDescription;

	private String middlewareId;

	private String middlewareTable;

	private String referenceDocumentType;

	private String referenceCycleCountNo;

}