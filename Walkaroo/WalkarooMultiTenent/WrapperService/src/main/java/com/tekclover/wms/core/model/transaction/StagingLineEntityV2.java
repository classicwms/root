package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class StagingLineEntityV2 extends StagingLineEntity {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private Double inventoryQuantity;
    private String manufacturerCode;
    private String manufacturerName;
    private String manufacturerFullName;
    private String origin;
    private String brand;
    private String barcodeId;
    private Double rec_accept_qty;
    private Double rec_damage_qty;

    private String middlewareId;
    private String middlewareTable;
    private String middlewareHeaderId;
    private String purchaseOrderNumber;
    private String referenceDocumentType;

    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private String parentProductionOrderNo;
    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
    private Long totalCount;
    private Long totalBarcodeStatusCount;
    private String vehicleNumber;
    private Long vehicleTotalHuNumber;
    private Long vehicleScanHuNumber;
    private String palletId;
    private String sapFlag;
    private String sapType;
    private String sapMessage;
    private String sapDocumentNo;
    private Date materialDocDate;
    private String putAwayQuantity;
    private Date putAwayDate;



}
