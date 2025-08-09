package com.almailem.ams.api.connector.model.picklist;

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
@Table(name = "PICKLISTLINE")
public class PickListLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Picklistlineid")
    private Long pickListLineId;

    @Column(name = "Picklistheaderid")
    private Long pickListHeaderId;

    @NotBlank(message = "Sales Order No is mandatory")
    @Column(name = "Salesorderno", columnDefinition = "nvarchar(50)", nullable = false)
    private String salesOrderNo;

    @NotBlank(message = "Pick List No is mandatory")
    @Column(name = "Picklistno", columnDefinition = "nvarchar(50)", nullable = false)
    private String pickListNo;

    @NotBlank(message = "Item Code is mandatory")
    @Column(name = "Itemcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String itemCode;

    @NotBlank(message = "Item Description is mandatory")
    @Column(name = "Itemdescription", columnDefinition = "nvarchar(500)", nullable = false)
    private String itemDescription;

    @NotNull(message = "Pick List Qty is mandatory")
    @Column(name = "Picklistqty")
    private Double pickListQty;

    @NotBlank(message = "Unit of Measure is mandatory")
    @Column(name = "Unitofmeasure", columnDefinition = "nvarchar(50)", nullable = false)
    private String unitOfMeasure;

    @NotBlank(message = "Manufacturer Code is mandatory")
    @Column(name = "Manufacturercode", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerCode;

    @NotBlank(message = "Manufacturer Short Name is mandatory")
    @Column(name = "Manufacturershortname", columnDefinition = "nvarchar(200)", nullable = false)
    private String manufacturerShortName;

    @Column(name = "Manufacturerfullname", columnDefinition = "nvarchar(250)")
    private String manufacturerFullName;

    @Column(name = "Pickedqty")
    private Double pickedQty;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "Is_cancelled", columnDefinition = "nvarchar(10)")
    private String isCancelled;

    @Column(name = "Is_all_picked", columnDefinition = "nvarchar(10)")
    private String isAllPicked;

    @NotNull(message = "Line Number of Each Item is mandatory")
    @Column(name = "Linenumberofeachitem", nullable = false)
    private Long lineNumberOfEachItem;
}
