package com.almailem.ams.api.connector.model.supplierinvoice;

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
@Table(name = "SUPPLIERINVOICEHEADER")
public class SupplierInvoiceHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Supplierinvoiceheaderid")
    private Long supplierInvoiceHeaderId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(25)", nullable = false)
    private String companyCode;

    @NotBlank(message = "Branch Code is mandatory")
    @Column(name = "Branchcode", columnDefinition = "nvarchar(25)", nullable = false)
    private String branchCode;

//    @NotBlank(message = "Purchase Order No is mandatory")
//    @Column(name = "PurchaseorderNo", columnDefinition = "nvarchar(50)")
//    private String purchaseOrderNo;

    @NotBlank(message = "Supplier Invoice No is mandatory")
    @Column(name = "Supplierinvoiceno", columnDefinition = "nvarchar(50)", nullable = false)
    private String supplierInvoiceNo;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "Updatedon")
    private Date updatedOn;

    @Column(name = "Is_cancelled", columnDefinition = "nvarchar(10)")
    private String isCancelled;

    //ProcessedStatusIdOrderByOrderReceivedOn
    @Column(name = "Processedstatusid", columnDefinition = "bigint default'0'")
    private Long processedStatusId = 0L;

    @Column(name = "Orderreceivedon", columnDefinition = "datetime2 default getdate()")
    private Date orderReceivedOn;

    @Column(name = "Orderprocessedon")
    private Date orderProcessedOn;

    @Column(name = "Amssupplierinvoiceno", columnDefinition = "nvarchar(50)")
    private String AMSSupplierInvoiceNo;

    @OneToMany(mappedBy = "supplierInvoiceHeaderId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SupplierInvoiceLine> supplierInvoiceLines;
}
