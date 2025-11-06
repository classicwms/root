package com.almailem.ams.api.connector.model.purchasereturn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PURCHASERETURNHEADER")
public class PurchaseReturnHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Purchasereturnheaderid")
    private Long purchaseReturnHeaderId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(50)", nullable = false)
    private String companyCode;

    @NotBlank(message = "Branch Code is mandatory")
    @Column(name = "Branchcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String branchCode;

    @NotBlank(message = "Return Order No is mandatory")
    @Column(name = "Returnorderno", columnDefinition = "nvarchar(50)", nullable = false)
    private String returnOrderNo;

    @NotNull(message = "Return Order Date is mandatory")
    @Column(name = "Returnorderdate")
    private Date returnOrderDate;

//    @Column(name = "SupplierInvoiceNo", columnDefinition = "nvarchar(50)")
//    private String supplierInvoiceNo;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "Updatedon")
    private Date updatedOn;

    @Column(name = "Is_cancelled", columnDefinition = "nvarchar(10)")
    private String isCancelled;

    //ProcessedStatusIdOrderByOrderReceivedOn
    @Column(name = "Processedstatusid", columnDefinition = "bigint default'0'")
    private Long processedStatusId = 0L;

    @Column(name = "Orderreceivedon")
    private Date orderReceivedOn;

    @Column(name = "Orderprocessedon")
    private Date orderProcessedOn;


    @OneToMany(mappedBy = "purchaseReturnHeaderId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PurchaseReturnLine> purchaseReturnLines;
}