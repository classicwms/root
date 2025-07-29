package com.tekclover.wms.api.inbound.orders.model.inbound.strategies;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbloutboundstrategies")
public class OutboundStrategies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OUT_STRATEGIES_ID")
    private Long outboundStrategiesId;

    @Column(name = "INV_ID")
    private Long inventoryId;

    @Column(name = "LANG_ID")
    private String languageId;

    @Column(name = "C_ID")
    private String companyCodeId;

    @Column(name = "PLANT_ID")
    private String plantId;

    @Column(name = "WH_ID")
    private String warehouseId;

    @Column(name = "PAL_CODE")
    private String palletCode;

    @Column(name = "CASE_CODE")
    private String caseCode;

    @Column(name = "PACK_BARCODE")
    private String packBarcodes;

    @Column(name = "ITM_CODE")
    private String itemCode;

    @Column(name = "MFR_NAME")
    private String manufacturerName;

    @Column(name = "VAR_ID")
    private Long variantCode;

    @Column(name = "VAR_SUB_ID")
    private String variantSubCode;

    @Column(name = "STR_NO")
    private String batchSerialNumber;

    @Column(name = "ST_BIN")
    private String storageBin;

    @Column(name = "STCK_TYP_ID")
    private Long stockTypeId;

    @Column(name = "SP_ST_IND_ID")
    private Long specialStockIndicatorId;

    @Column(name = "BARCODE_ID", columnDefinition = "nvarchar(255)")
    private String barcodeId;

    @Column(name = "CBM", columnDefinition = "nvarchar(255)")
    private String cbm;

    @Column(name = "CBM_UNIT", columnDefinition = "nvarchar(255)")
    private String cbmUnit;

    @Column(name = "CBM_PER_QTY", columnDefinition = "nvarchar(255)")
    private String cbmPerQuantity;

    @Column(name = "INV_QTY")
    private Double inventoryQuantity;

    @Column(name = "ALLOC_QTY")
    private Double allocatedQuantity;

    @Column(name = "INV_UOM")
    private String inventoryUom;

    @Column(name = "FIFO_MD")
    private String fifoMethod;

    @Column(name = "OB_STRATEGIES")
    private String outboundStrategies;

    @Column(name = "ITEM_TEXT")
    private String itemDescription;

    @Column(name = "SEQ_NO")
    private Long sequenceNo;

    @Column(name = "PARTNER_NAME")
    private String partnerName;

    @Column(name = "REF_ORD_NO")
    private String referenceOrderNo;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator = 0L;

    @Column(name = "IU_CTD_BY")
    private String createdBy;

    @Column(name = "IU_CTD_ON")
    private Date createdOn;

    @Column(name = "UTD_BY")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;

}
