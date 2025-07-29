package com.almailem.ams.api.connector.model.stockreceipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "STOCKRECEIPTHEADER")
public class StockReceiptHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Stockreceiptheaderid")
    private Long stockReceiptHeaderId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(25)", nullable = false)
    private String companyCode;

    @NotBlank(message = "Branch Code is mandatory")
    @Column(name = "Branchcode", columnDefinition = "nvarchar(25)", nullable = false)
    private String branchCode;

    @NotBlank(message = "Receipt Number is mandatory")
    @Column(name = "Receiptno", columnDefinition = "nvarchar(50)", nullable = false)
    private String receiptNo;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "Updatedon")
    private Date updatedOn;

    //ProcessedStatusIdOrderByOrderReceivedOn
    @Column(name = "Processedstatusid", columnDefinition = "bigint default'0'")
    private Long processedStatusId = 0L;

    @Column(name = "Orderreceivedon", columnDefinition = "datetime2 default getdate()")
    private Date orderReceivedOn;

    @Column(name = "Orderprocessedon")
    private Date orderProcessedOn;

    @OneToMany(mappedBy = "stockReceiptHeaderId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<StockReceiptLine> stockReceiptLines;
}
