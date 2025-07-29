package com.almailem.ams.api.connector.model.periodic;

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

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PERIODICLINE")
public class PeriodicLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Periodiclineid")
    private Long periodicLineId;

    @Column(name = "Periodicheaderid")
    private Long periodicHeaderId;

    @NotBlank(message = "Cycle Count No is mandatory")
    @Column(name = "Cyclecountno", columnDefinition = "nvarchar(50)", nullable = false)
    private String cycleCountNo;

    @NotNull(message = "line NO Of Each Item Code is mandatory")
    @Column(name = "Linenoofeachitemcode")
    private Long lineNoOfEachItemCode;

    @NotBlank(message = "Item Code is mandatory")
    @Column(name = "Itemcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String itemCode;

    @Column(name = "Itemdescription", columnDefinition = "nvarchar(500)")
    private String itemDescription;

    @NotBlank(message = "Unit of Measure is mandatory")
    @Column(name = "Unitofmeasure", columnDefinition = "nvarchar(50)", nullable = false)
    private String unitOfMeasure;

    @NotBlank(message = "Manufacturer Code is mandatory")
    @Column(name = "Manufacturercode", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerCode;

    @NotBlank(message = "Manufacturer Name is mandatory")
    @Column(name = "Manufacturername", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerName;

    @NotNull(message = "FrozenQty is mandatory")
    @Column(name = "Frozenqty")
    private Double frozenQty;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "Is_cancelled", columnDefinition = "nvarchar(20)")
    private String isCancelled;

    @Column(name = "Countedqty")
    private Double countedQty;
}
