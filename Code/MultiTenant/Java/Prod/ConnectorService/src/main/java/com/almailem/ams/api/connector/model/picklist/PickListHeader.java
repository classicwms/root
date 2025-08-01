package com.almailem.ams.api.connector.model.picklist;

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
@Table(name = "PICKLISTHEADER")
public class PickListHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Picklistheaderid")
        private Long pickListHeaderId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(50)", nullable = false)
    private String companyCode;

    @NotBlank(message = "Branch Code is mandatory")
    @Column(name = "Branchcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String branchCode;

    @NotBlank(message = "Sales Order No is mandatory")
    @Column(name = "Salesorderno", columnDefinition = "nvarchar(50)", nullable = false)
    private String salesOrderNo;

    @Column(name = "Tokennumber", columnDefinition = "nvarchar(50)")
    private String tokenNumber;

    @Column(name = "IMSSaleTypeCode")
    private Integer imsSaleTypeCode;

    @NotBlank(message = "Pick List No is mandatory")
    @Column(name = "Picklistno", columnDefinition = "nvarchar(50)", nullable = false)
    private String pickListNo;

    @NotNull(message = "Pick List Date is mandatory")
    @Column(name = "Picklistdate")
    private Date pickListdate;

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


    @OneToMany(mappedBy = "pickListHeaderId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PickListLine> pickListLines;
}
