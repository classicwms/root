package com.tekclover.wms.core.model.idmaster;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class InterimBarcode { 
	
    private Long interimBarcodeId = 0L;
	private String storageBin;
	private String itemCode;
	private String barcode;
	private Long deletionIndicator = 0L;
	private String createdBy;
    private Date createdOn;
    private String updatedBy;
	private Date updatedOn;
}
