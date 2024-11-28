package com.tekclover.wms.api.transaction.model.threepl.stockmovement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID', 'C_ID', 'PLANT_ID', 'WH_ID', 'MVT_DOC_NO`, 'ITM_CODE'
 */
@Table(
        name = "tblstockmovement",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_stockmovement",
                        columnNames = {"MVT_DOC_NO", "LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_CODE"})
        }
)
@IdClass(StockMovementCompositeKey.class)
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MVT_DOC_NO")
    private Long movementDocNo;

    @Id
    @Column(name = "LANG_ID", columnDefinition = "nvarchar(5)")
    private String languageId;

    @Id
    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyCodeId;

    @Id
    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
    private String plantId;

    @Id
    @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Id
    @Column(name = "ITM_CODE", columnDefinition = "nvarchar(50)" )
    private String itemCode;

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(50)")
    private String refDocNumber;

    @Column(name = "DOC_DATE")
    private Date documentDate = new Date();

    @Column(name = "OPEN_STOCK")
    private Double openingStock;

    @Column(name = "IB_QTY")
    private Double inboundQty;

    @Column(name = "OB_QTY")
    private Double outboundQty;

    @Column(name = "CLOSE_STOCK")
    private Double closingStock;

    @Column(name = "STOCK_UOM")
    private String stockUom;

    @Column(name = "TEXT", columnDefinition = "nvarchar(200)")
    private String description;

    @Column(name = "C_DESC", columnDefinition = "nvarchar(500)")
    private String companyDescription;

    @Column(name = "PLANT_DESC",columnDefinition = "nvarchar(500)")
    private String plantDescription;

    @Column(name = "WAREHOUSE_DESC",columnDefinition = "nvarchar(500)")
    private String warehouseDescription;

    @Column(name = "STATUS_DESC",columnDefinition = "nvarchar(500)")
    private String statusDescription;

    @Column(name = "STATUS_ID")
    private Long statusId;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "REF_FIELD_1", columnDefinition = "nvarchar(200)")
    private String referenceField1;

    @Column(name = "REF_FIELD_2", columnDefinition = "nvarchar(200)")
    private String referenceField2;

    @Column(name = "REF_FIELD_3", columnDefinition = "nvarchar(200)")
    private String referenceField3;

    @Column(name = "REF_FIELD_4", columnDefinition = "nvarchar(200)")
    private String referenceField4;

    @Column(name = "REF_FIELD_5", columnDefinition = "nvarchar(200)")
    private String referenceField5;

    @Column(name = "REF_FIELD_6", columnDefinition = "nvarchar(200)")
    private String referenceField6;

    @Column(name = "REF_FIELD_7", columnDefinition = "nvarchar(200)")
    private String referenceField7;

    @Column(name = "REF_FIELD_8", columnDefinition = "nvarchar(200)")
    private String referenceField8;

    @Column(name = "REF_FIELD_9", columnDefinition = "nvarchar(200)")
    private String referenceField9;

    @Column(name = "REF_FIELD_10", columnDefinition = "nvarchar(200)")
    private String referenceField10;

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY", columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;


}
