package com.almailem.ams.api.connector.model.transferout;

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
@Table(name = "TRANSFEROUTLINE")
public class TransferOutLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Transferoutlineid")
    private Long transferOutLineId;

    @Column(name = "Transferoutheaderid")
    private Long transferOutHeaderId;

    @NotBlank(message = "Transfer Order Number is mandatory")
    @Column(name = "Transferordernumber", columnDefinition = "nvarchar(50)", nullable = false)
    private String transferOrderNumber;

    @NotNull(message = "Line Number of Each Item is mandatory")
    @Column(name = "Linenumberofeachitem")
    private Long lineNumberOfEachItem;

    @NotBlank(message = "Item Code is mandatory")
    @Column(name = "Itemcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String itemCode;

    @NotBlank(message = "Item Description is mandatory")
    @Column(name = "Itemdescription", columnDefinition = "nvarchar(500)", nullable = false)
    private String itemDescription;

    @NotNull(message = "Transfer Order Qty is mandatory")
    @Column(name = "Transferorderqty")
    private Double transferOrderQty;

    @NotBlank(message = "UOM is mandatory")
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

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

}
