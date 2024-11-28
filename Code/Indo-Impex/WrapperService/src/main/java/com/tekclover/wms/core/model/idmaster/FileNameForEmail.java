package com.tekclover.wms.core.model.idmaster;

import lombok.Data;

import java.util.Date;

@Data
public class FileNameForEmail {

	private Long fileNameId;
	private String dispatch110;
	private String delivery110;
	private String dispatch111;
	private String delivery111;
	private String groupBy;
	private Long deletionIndicator;
	private String reportDate;
}
