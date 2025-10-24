package com.almailem.ams.api.connector.model.transferout;

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
@Table(name = "TRANSFEROUTHEADER")
public class TransferOutHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Transferoutheaderid")
    private Long transferOutHeaderId;

    @NotBlank(message = "Source Company Code is mandatory")
    @Column(name = "Sourcecompanycode", columnDefinition = "nvarchar(50)", nullable = false)
    private String sourceCompanyCode;

    @NotBlank(message = "Target Company Code is mandatory")
    @Column(name = "Targetcompanycode", columnDefinition = "nvarchar(50)", nullable = false)
    private String targetCompanyCode;

    @NotBlank(message = "Transfer Order Number is mandatory")
    @Column(name = "Transferordernumber", columnDefinition = "nvarchar(50)", nullable = false)
    private String transferOrderNumber;

    @NotBlank(message = "Source Branch Code is mandatory")
    @Column(name = "Sourcebranchcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String sourceBranchCode;

    @NotBlank(message = "Target Branch Code is mandatory")
    @Column(name = "Targetbranchcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String targetBranchCode;

    @NotNull(message = "Transfer Order Date is mandatory")
    @Column(name = "Transferorderdate")
    private Date transferOrderDate;

    @Column(name = "Fulfilmentmethod", columnDefinition = "nvarchar(10)")
    private String fulfilmentMethod;

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

    @Column(name = "Transferrequesttype", columnDefinition = "nvarchar(50)")
    private String TransferRequestType;

    @OneToMany(mappedBy = "transferOutHeaderId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TransferOutLine> transferOutLines;
}