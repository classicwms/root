package com.almailem.ams.api.connector.model.salesreturn;

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
@Table(name = "SALESRETURNHEADER")
public class SalesReturnHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Salesreturnheaderid")
    private Long salesReturnHeaderId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(25)", nullable = false)
    private String companyCode;

//    @NotBlank(message = "Branch Code of Receiving Warehouse is mandatory")
    @Column(name = "Branchcodeofreceivingwarehouse", columnDefinition = "nvarchar(25)")
    private String branchCodeOfReceivingWarehouse;

    @NotBlank(message = "Branch Code is mandatory")
    @Column(name = "Branchcode", columnDefinition = "nvarchar(25)")
    private String branchCode;

    @NotBlank(message = "Return Order No is mandatory")
    @Column(name = "Returnorderno", columnDefinition = "nvarchar(50)", nullable = false)
    private String returnOrderNo;

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

    @Column(name = "Customercode", columnDefinition = "nvarchar(50)")
    private String customerCode;

    @OneToMany(mappedBy = "salesReturnHeaderId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SalesReturnLine> salesReturnLines;
}