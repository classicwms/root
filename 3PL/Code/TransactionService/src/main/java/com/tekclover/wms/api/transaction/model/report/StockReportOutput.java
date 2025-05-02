package com.tekclover.wms.api.transaction.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor@Table(name = "tblstockreportoutput")
public class StockReportOutput {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STK_ID")
	private Long stockId;

	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
	private String companyCodeId;

	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
	private String plantId;

	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
	private String languageId;

	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
	private String warehouseId;

	@Column(name = "ITM_CODE", columnDefinition = "nvarchar(255)")
	private String itemCode;            // ITM_CODE

	@Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
	private String manufacturerName;    // MFR_SKU

	@Column(name = "TEXT", columnDefinition = "nvarchar(255)")
	private String itemText;            // ITEM_TEXT

	@Column(name = "INV_QTY")
	private Double invQty;            // INV_QTY

	@Column(name = "ALLOC_QTY")
	private Double allocQty;            // Alloc Qty

	@Column(name = "TOT_QTY")
	private Double totalQty;                // Total Qty

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT",columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT",columnDefinition = "nvarchar(255)")
	private String warehouseDescription;

	@Column(name = "PARTNER_CODE",columnDefinition = "nvarchar(255)")
	private String partnerCode;

}