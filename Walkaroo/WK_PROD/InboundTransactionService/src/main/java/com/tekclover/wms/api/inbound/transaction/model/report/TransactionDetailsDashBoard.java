package com.tekclover.wms.api.inbound.transaction.model.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltransactiondetailsdashboard")
public class TransactionDetailsDashBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(25)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
    private String plantId;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
    private String languageId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;

    @Column(name = "PROCESS", columnDefinition = "nvarchar(50)")
    private String process;

    @Column(name = "ITM_CODE", columnDefinition = "nvarchar(100)")
    private String itemCode;

    @Column(name = "BAR_ID", columnDefinition = "nvarchar(100)")
    private String barcodeId;

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "UTD_BY", columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "CTD_ON")
    private Date createdOn;

    @Column(name = "UTD_ON")
    private Date updatedOn;

    @Column(name = "QTY")
    private Double quantity;

    @Column(name = "IS_READ", columnDefinition = "bit default '0'")
    private boolean isRead;
}