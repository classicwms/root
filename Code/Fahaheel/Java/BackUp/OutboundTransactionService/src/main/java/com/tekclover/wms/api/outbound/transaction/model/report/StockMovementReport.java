package com.tekclover.wms.api.outbound.transaction.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblstockmovementreport")
public class StockMovementReport {

    @Id
    @Column(name = "STOCK_MOVEMENTREPORT_ID")
    private Long stockMovementReportId;            // STOCK_MOVEMENTREPORT_ID

    @Column(name = "WH_ID")
    private String warehouseId;                    // WH_ID
    @Column(name = "C_ID")
    private String companyCodeId;                    // C_ID
    @Column(name = "PLANT_ID")
    private String plantId;                    // PLANT_ID
    @Column(name = "LANG_ID")
    private String languageId;                    // LANG_ID

     @Column(name = "WH_TEXT")
    private String warehouseDescription;                    // WH_ID
    @Column(name = "C_TEXT")
    private String companyDescription;                    // C_ID
    @Column(name = "PLANT_TEXT")
    private String plantDescription;                    // PLANT_ID
    @Column(name = "STATUS_TEXT")
    private String statusDescription;                    // LANG_ID

    @Column(name = "ITM_CODE")
    private String itemCode;                    // ITM_CODE

    @Column(name = "MFR_SKU")
    private String manufacturerSKU;            // MFR_SKU

    @Column(name = "ITEM_TEXT")
    private String itemText;                    // ITEM_TEXT

    @Column(name = "MVT_QTY")
    private Double movementQty;                    // MOV_QTY

    @Column(name = "DOC_TYPE")
    private String documentType;                // Document type

    @Column(name = "DOC_NO")
    private String documentNumber;                // Document Number

    @Column(name = "CUSTOMER_CODE")
    private String customerCode;                // Customer Code

    @Column(name = "CTD_ON")
    private String createdOn;                    // IM_CTD_ON date

    @Column(name = "CREATED_TIME")
    private String createdTime;                    // IM_CTD_ON Time

    @Column(name = "BALANCE_OH_QTY")
    private Double balanceOHQty;                // Document Number

    @Column(name = "OPENING_STOCK")
    private Double openingStock;                // Opening Stock

    @Column(name = "CONFIRMED_ON")
    private Date confirmedOn;

    @Column(name = "ST_BIN")
    private String storageBin;
}
