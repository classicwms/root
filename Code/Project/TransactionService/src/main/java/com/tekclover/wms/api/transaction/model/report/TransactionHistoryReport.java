package com.tekclover.wms.api.transaction.model.report;

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

    private String warehouseId;

    private String itemCode;

    @Column(name = "DESCRIPTION")
    private String itemDescription;

    private Double closingStock;

    private Double openingStock;

    private Double inboundQty;

    private Double outboundQty;

    private Double stockAdjustmentQty;

    private Double systemInventory;
}
