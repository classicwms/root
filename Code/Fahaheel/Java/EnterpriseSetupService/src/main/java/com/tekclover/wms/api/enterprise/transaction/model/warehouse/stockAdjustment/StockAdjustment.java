package com.tekclover.wms.api.enterprise.transaction.model.warehouse.stockAdjustment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity(name = "middlewareStockAdjustment")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmiddlewarestockadjustment")
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StockAdjustmentId")
    private Long stockAdjustmentId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "CompanyCode", columnDefinition = "nvarchar(50)",nullable = false)
    private String companyCode;

    @NotBlank(message = "Stock Count Branch Code is mandatory")
    @Column(name = "StockcountBranchcode", columnDefinition = "nvarchar(50)",nullable = false)
    private String branchCode;

    @Column(name = "BranchName", columnDefinition = "nvarchar(500)")
    private String branchName;

    @NotNull(message = "Date of Adjustment is mandatory")
    @Column(name = "Dateofadjustment",nullable = false)
    private Date dateOfAdjustment;

    @Column(name = "IS_CYCLECOUNT", columnDefinition = "nvarchar(10)")
    private String isCycleCount;

    @Column(name = "IS_DAMAGE", columnDefinition = "nvarchar(10)")
    private String isDamage;

    @NotBlank(message = "Item Code is mandatory")
    @Column(name = "Itemcode", columnDefinition = "nvarchar(50)",nullable = false)
    private String itemCode;

    @Column(name = "Itemdescription", columnDefinition = "nvarchar(500)")
    private String itemDescription;

    @NotNull(message = "Adjustment Qty is mandatory")
    @Column(name = "Adjustmentqty",nullable = false)
    private Double adjustmentQty;

    @NotBlank(message = "Unit of Measure is mandatory")
    @Column(name = "UnitofMeasure", columnDefinition = "nvarchar(50)", nullable = false)
    private String unitOfMeasure;

    @NotBlank(message = "Manufacturer Code is mandatory")
    @Column(name = "ManufacturerCode", columnDefinition = "nvarchar(200)",nullable = false)
    private String manufacturerCode;

    @NotBlank(message = "Manufacturer Name is mandatory")
    @Column(name = "ManufacturerName", columnDefinition = "nvarchar(200)",nullable = false)
    private String manufacturerName;

    @Column(name = "Remarks", columnDefinition = "nvarchar(250)")
    private String remarks;

    @Column(name = "AMSreferenceNo", columnDefinition = "nvarchar(50)")
    private String amsReferenceNo;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    private String warehouseId;
    private String refDocType;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;

    @Column(name = "Updatedon")
    private Date updatedOn;

    //ProcessedStatusIdOrderByOrderReceivedOn
    @Column(name = "processedStatusId", columnDefinition = "bigint default'0'")
    private Long processedStatusId = 0L;
    @Column(name = "orderReceivedOn", columnDefinition = "datetime2 default getdate()")
    private Date orderReceivedOn;
    private Date orderProcessedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}