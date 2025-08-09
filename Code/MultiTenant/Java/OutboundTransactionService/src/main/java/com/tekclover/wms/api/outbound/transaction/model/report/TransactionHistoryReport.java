package com.tekclover.wms.api.outbound.transaction.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltransactionhistoryresults")
public class TransactionHistoryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "company_code_id")
    private String companyCodeId;
    @Column(name = "plant_id")
    private String plantId;
    @Column(name = "language_id")
    private String languageId;
    @Column(name = "warehouse_id")
    private String warehouseId;
    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "DESCRIPTION")
    private String itemDescription;
    @Column(name = "C_TEXT")
    private String companyDescription;
    @Column(name = "PLANT_TEXT")
    private String plantDescription;
    @Column(name = "WH_TEXT")
    private String warehouseDescription;
    @Column(name = "MFR_NAME")
    private String manufacturerName;

    private Double variance;

    @Column(name = "closing_stock")
    private Double closingStock;

    @Column(name = "opening_stock")
    private Double openingStock;

    @Column(name = "inbound_qty")
    private Double inboundQty;

    @Column(name = "outbound_qty")
    private Double outboundQty;

    @Column(name = "stock_adjustment_qty")
    private Double stockAdjustmentQty;

    @Column(name = "system_inventory")
    private Double systemInventory;
}