package com.tekclover.wms.api.inbound.transaction.model.crossdock;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblcrossdockgrline")
public class CrossDockGrLine {

    @Id
    @Column(name = "CROSS_DOCK_GR_NO")
    private String crossDockGrNumber;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
    private String languageId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(25)")
    private String companyCode;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
    private String plantId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;

    @Column(name = "PRE_IB_NO")
    private String preInboundNo;

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)")
    private String refDocNumber;

    @Column(name = "GR_NO")
    private String goodsReceiptNo;

    @Column(name = "PAL_CODE")
    private String palletCode;

    @Column(name = "CASE_CODE")
    private String caseCode;

    @Column(name = "PACK_BARCODE", columnDefinition = "nvarchar(50)")
    private String packBarcodes;

    @Column(name = "IB_LINE_NO")
    private Long lineNo;

    @Column(name = "NO_BAGS")
    private Double noBags;

    @Column(name = "ITM_CODE", columnDefinition = "nvarchar(255)")
    private String itemCode;

    @Column(name = "ORD_QTY")
    private Double orderQty;

    @Column(name = "GR_QTY")
    private Double goodReceiptQty;

    @Column(name = "ACCEPT_QTY")
    private Double acceptedQty;

    @Column(name = "DAMAGE_QTY")
    private Double damageQty;


    @Column(name = "QTY_TYPE")
    private String quantityType;

    @Column(name = "REM_QTY")
    private Double remainingQty;

    @Column(name = "REF_ORD_QTY")
    private Double referenceOrderQty;

    @Column(name = "CROSS_DOCK_ALLOC_QTY")
    private Double crossDockAllocationQty;


    @Column(name = "STR_QTY")
    private Double storageQty;


    @Column(name = "REF_FIELD_1")
    private String referenceField1;

    @Column(name = "REF_FIELD_2")
    private String referenceField2;

    @Column(name = "REF_FIELD_3")
    private String referenceField3;

    @Column(name = "REF_FIELD_4")
    private String referenceField4;

    @Column(name = "REF_FIELD_5")
    private String referenceField5;

    @Column(name = "REF_FIELD_6")
    private String referenceField6;

    @Column(name = "REF_FIELD_7")
    private String referenceField7;

    @Column(name = "REF_FIELD_8")
    private String referenceField8;

    @Column(name = "REF_FIELD_9")
    private String referenceField9;

    @Column(name = "REF_FIELD_10")
    private String referenceField10;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "INV_QTY")
    private Double inventoryQuantity;

    @Column(name = "PA_NO", columnDefinition = "nvarchar(100)")
    private String putAwayNumber;
}

