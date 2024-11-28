package com.tekclover.wms.api.transaction.model.report;

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
@Table(name = "tblstockmovementreport1") 
public class StockMovementReport1 {

	@Id
	@Column(name = "STOCK_MOVEMENTREPORT_ID")
	private Long stockMovementReportId;			// STOCK_MOVEMENTREPORT_ID
	
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
	private String warehouseId;					// WH_ID
	
	@Column(name = "ITM_CODE", columnDefinition = "nvarchar(255)")
	private String itemCode;					// ITM_CODE
	
	@Column(name = "MFR_SKU")
	private String manufacturerSKU; 			// MFR_SKU
	
	@Column(name = "ITEM_TEXT")
	private String itemText;					// ITEM_TEXT
	
	@Column(name = "MVT_QTY")
	private Double movementQty;					// MOV_QTY
	
	@Column(name = "DOC_TYPE")
	private String documentType;				// Document type

	@Column(name = "DOC_NO")
	private String documentNumber;				// Document Number
	
	@Column(name = "CUSTOMER_CODE")
	private String customerCode;				// Customer Code
	
	@Column(name = "CTD_ON")
	private String createdOn;					// IM_CTD_ON date
	
	@Column(name = "CREATED_TIME")
	private String createdTime;					// IM_CTD_ON Time
	
	@Column(name = "BALANCE_OH_QTY")
	private Double balanceOHQty;				// Document Number

	@Column(name = "OPENING_STOCK")
	private Double openingStock;				// Opening Stock

	@Column(name = "CONFIRMED_ON")
	private Date confirmedOn;
}