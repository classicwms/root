package com.almailem.ams.api.connector.model.periodic;

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
@Table(name = "PERIODICHEADER")
public class PeriodicHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Periodicheaderid")
    private Long periodicHeaderId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(50)", nullable = false)
    private String companyCode;

    @NotBlank(message = "Cycle Count No is mandatory")
    @Column(name = "Cyclecountno", columnDefinition = "nvarchar(50)", nullable = false)
    private String cycleCountNo;

    @NotBlank(message = "Branch Code is mandatory")
    @Column(name = "Branchcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String branchCode;

    @Column(name = "Branchname", columnDefinition = "nvarchar(500)")
    private String branchName;

    @NotNull(message = "Cycle Count Creation Date is mandatory")
    @Column(name = "Cyclecountcreationdate")
    private Date cycleCountCreationDate;

    @Column(name = "Is_new", columnDefinition = "nvarchar(20)", nullable = false)
    private String isNew;

    @Column(name = "Is_cancelled", columnDefinition = "nvarchar(20)", nullable = false)
    private String isCancelled;

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


    @OneToMany(mappedBy = "periodicHeaderId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PeriodicLine> periodicLines;
}
