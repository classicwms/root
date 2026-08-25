package com.tekclover.wms.api.inbound.orders.model.report;

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

    private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;

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
    private Double closingStock;

    private Double openingStock;

    private Double inboundQty;

    private Double outboundQty;

    private Double stockAdjustmentQty;

    private Double systemInventory;
}
