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
@Table(name = "tblcrossdockordermanagementline")
public class CrossDockUnallocated {

    @Id
    @Column(name = "CROSS_DOCK_ORDER_NO")
    private String crossDockOrderManagementNo;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
    private String languageId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(25)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
    private String plantId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;

    @Column(name = "PRE_OB_NO", columnDefinition = "nvarchar(50)")
    private String preOutboundNo;

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)")
    private String refDocNumber;

    @Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(100)")
    private String partnerCode;

    @Column(name = "OB_LINE_NO")
    private Long lineNumber;

    @Column(name = "ITM_CODE", columnDefinition = "nvarchar(255)")
    private String itemCode;

    @Column(name = "PROP_ST_BIN", columnDefinition = "nvarchar(100)")
    private String proposedStorageBin;

    @Column(name = "PROP_PACK_BARCODE", columnDefinition = "nvarchar(50)")
    private String proposedPackBarCode; //proposedPackCode

    @Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(100)")
    private String barcodeId;

    @Column(name = "ORD_QTY")
    private Double orderQty;

    @Column(name = "ORD_UOM")
    private String orderUom;

    @Column(name = "ALLOC_QTY")
    private Double allocatedQty;

    @Column(name = "RE_ALLOC_QTY")
    private Double reAllocatedQty;

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

    @Column(name = "PU_NO", columnDefinition = "nvarchar(50)")
    private String pickupNumber;

    @Column(name = "NO_BAGS")
    private Double noBags;

}
