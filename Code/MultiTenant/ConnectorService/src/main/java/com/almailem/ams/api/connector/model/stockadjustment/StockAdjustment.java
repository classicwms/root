package com.almailem.ams.api.connector.model.stockadjustment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "STOCKADJUSTMENT")
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Stockadjustmentid")
    private Long stockAdjustmentId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(50)", nullable = false)
    private String companyCode;

    @NotBlank(message = "Stock Count Branch Code is mandatory")
    @Column(name = "Stockcountbranchcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String branchCode;

    @Column(name = "Branchname", columnDefinition = "nvarchar(500)")
    private String branchName;

    @NotNull(message = "Date of Adjustment is mandatory")
    @Column(name = "Dateofadjustment")
    private Date dateOfAdjustment;

//    @Column(name = "Is_cyclecount", columnDefinition = "nvarchar(10)", nullable = false)
    @Column(name = "Is_cyclecount", columnDefinition = "nvarchar(10)")
    private String isCycleCount;

    @Column(name = "Is_damage", columnDefinition = "nvarchar(10)", nullable = false)
    private String isDamage;

    @NotBlank(message = "Item Code is mandatory")
    @Column(name = "Itemcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String itemCode;

    @Column(name = "Itemdescription", columnDefinition = "nvarchar(500)")
    private String itemDescription;

    @NotNull(message = "Adjustment Qty is mandatory")
    @Column(name = "Adjustmentqty")
    private Double adjustmentQty;

    @NotBlank(message = "Unit of Measure is mandatory")
    @Column(name = "Unitofmeasure", columnDefinition = "nvarchar(50)", nullable = false)
    private String unitOfMeasure;

    @NotBlank(message = "Manufacturer Code is mandatory")
    @Column(name = "Manufacturercode", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerCode;

//    @NotBlank(message = "Manufacturer Name is mandatory")
    @Column(name = "Manufacturername", columnDefinition = "nvarchar(200)")
    private String manufacturerName;

    @Column(name = "Remarks", columnDefinition = "nvarchar(250)")
    private String remarks;

    @Column(name = "Amsreferenceno", columnDefinition = "nvarchar(50)")
    private String amsReferenceNo;

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
}