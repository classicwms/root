package com.tekclover.wms.api.transaction.model.outbound.packing;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "tblpackinginventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_packinginventory",
                        columnNames = {"LANG_ID"," C_ID", "PLANT_ID"," WH_ID", "PACK_MAT_NO"}
                )
        }
)
@IdClass(PackingInventoryCompositeKey.class)
public class PackingInventory {

    @Id
    @Column(name = "LANG_ID", columnDefinition = "nvarchar(50)")
    private String languageId;

    @Id
    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyId;

    @Id
    @Column(name = "PLANT_ID",columnDefinition = "nvarchar(50)")
    private String plantId;

    @Id
    @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Id
    @Column(name = "PACK_MAT_NO", columnDefinition = "nvarchar(50)")
    private String packingMaterialNo;

    @Column(name = "TNV_QTY")
    private Double inventoryQty;

    @Column(name = "INV_UOM", columnDefinition = "nvarchar(50)")
    private String inventoryUom;

    @Column(name = "STATUS_ID")
    private Long statusId;

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

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY", columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn = new Date();
}
