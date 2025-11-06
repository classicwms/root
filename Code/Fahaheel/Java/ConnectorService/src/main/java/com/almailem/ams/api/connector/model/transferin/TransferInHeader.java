package com.almailem.ams.api.connector.model.transferin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TRANSFERINHEADER")
public class TransferInHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Transferinheaderid")
    private Long transferInHeaderId;

    @NotBlank(message = "SourceCompanyCode is mandatory")
    @Column(name = "Sourcecompanycode", columnDefinition = "nvarchar(25)", nullable = false)
    private String sourceCompanyCode;

    @NotBlank(message = "TargetCompanyCode is mandatory")
    @Column(name = "Targetcompanycode", columnDefinition = "nvarchar(25)", nullable = false)
    private String targetCompanyCode;

    @NotBlank(message = "TransferOrderNo is mandatory")
    @Column(name = "Transferorderno", columnDefinition = "nvarchar(50)", nullable = false)
    private String transferOrderNo;

    @NotBlank(message = "SourceBranchCode is mandatory")
    @Column(name = "Sourcebranchcode", columnDefinition = "nvarchar(25)", nullable = false)
    private String sourceBranchCode;

    @NotBlank(message = "TargetBranchCode is mandatory")
    @Column(name = "Targetbranchcode", columnDefinition = "nvarchar(25)", nullable = false)
    private String targetBranchCode;

    @NotNull(message = "TransferOrder Date is mandatory")
    @Column(name = "Transferorderdate")
    private Date transferOrderDate;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "Updatedon")
    private Date updatedOn;

    //ProcessedStatusIdOrderByOrderReceivedOn
    @Column(name = "Processedstatusid", columnDefinition = "bigint default'0'")
    private Long processedStatusId = 0L;

    @Column(name = "Orderreceivedon")
    private Date orderReceivedOn;

    @Column(name = "Orderprocessedon")
    private Date orderProcessedOn;

    @Column(name = "Transferrequesttype", columnDefinition = "nvarchar(50)")
    private String TransferRequestType;

    @OneToMany(mappedBy = "transferInHeaderId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TransferInLine> transferInLines;
}