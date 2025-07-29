package com.tekclover.wms.api.outbound.transaction.model.inbound.putaway.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblpalletidassignment")
public class PalletIdAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "P_ID")
    private Long paId;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(50)")
    private String languageId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
    private String plantId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Column(name = "PRE_IB_NO", columnDefinition = "nvarchar(100)")
    private String preInboundNo;

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)")
    private String refDocNumber;

    @Column(name = "IB_ORD_TYP_ID")
    private Long inboundOrderTypeId;

    @Column(name = "PAL_CODE", columnDefinition = "nvarchar(100)")
    private String palletCode;

    @Column(name = "PAL_ID", columnDefinition = "nvarchar(100)")
    private String palletId;

    @Column(name = "CASE_CODE", columnDefinition = "nvarchar(100)")
    private String caseCode;

    @Column(name = "PACK_BARCODE", columnDefinition = "nvarchar(100)")
    private String packBarcodes;

    @Column(name = "PROP_ST_BIN", columnDefinition = "nvarchar(100)")
    private String proposedStorageBin;

    @Column(name = "ASS_USER_ID", columnDefinition = "nvarchar(100)")
    private String assignedUserId;

    @Column(name = "STATUS_ID", columnDefinition = "bigint default '0'")
    private Long statusId;

    @Column(name = "ST_SEC_ID", columnDefinition = "nvarchar(25)")
    private String storageSectionId;

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(100)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(100)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(100)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(100)")
    private String statusDescription;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(100)")
    private String referenceDocumentType;

    @Column(name = "LEVEL_ID", columnDefinition = "nvarchar(50)")
    private String levelId;

    @Column(name = "MATERIAL_NO", columnDefinition = "nvarchar(50)")
    private String materialNo;

    @Column(name = "PRICE_SEGMENT", columnDefinition = "nvarchar(50)")
    private String priceSegment;

    @Column(name = "ARTICLE_NO", columnDefinition = "nvarchar(50)")
    private String articleNo;

    @Column(name = "GENDER", columnDefinition = "nvarchar(50)")
    private String gender;

    @Column(name = "COLOR", columnDefinition = "nvarchar(50)")
    private String color;

    @Column(name = "SIZE", columnDefinition = "nvarchar(50)")
    private String size;

    @Column(name = "NO_PAIRS", columnDefinition = "nvarchar(50)")
    private String noPairs;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator;

    @Column(name = "REF_FIELD_1", columnDefinition = "nvarchar(100)")
    private String referenceField1;

    @Column(name = "REF_FIELD_2", columnDefinition = "nvarchar(100)")
    private String referenceField2;

    @Column(name = "REF_FIELD_3", columnDefinition = "nvarchar(100)")
    private String referenceField3;

    @Column(name = "REF_FIELD_4", columnDefinition = "nvarchar(100)")
    private String referenceField4;

    @Column(name = "REF_FIELD_5", columnDefinition = "nvarchar(100)")
    private String referenceField5;

    @Column(name = "REF_FIELD_6", columnDefinition = "nvarchar(100)")
    private String referenceField6;

    @Column(name = "REF_FIELD_7", columnDefinition = "nvarchar(100)")
    private String referenceField7;

    @Column(name = "REF_FIELD_8", columnDefinition = "nvarchar(100)")
    private String referenceField8;

    @Column(name = "REF_FIELD_9", columnDefinition = "nvarchar(100)")
    private String referenceField9;

    @Column(name = "REF_FIELD_10", columnDefinition = "nvarchar(100)")
    private String referenceField10;

    @Column(name = "PA_CTD_BY", columnDefinition = "nvarchar(100)")
    private String createdBy;

    @Column(name = "PA_CTD_ON")
    private Date createdOn;

    @Column(name = "PA_CNF_BY", columnDefinition = "nvarchar(100)")
    private String confirmedBy;

    @Column(name = "PA_CNF_ON")
    private Date confirmedOn;

    @Column(name = "PA_ASGN_BY", columnDefinition = "nvarchar(100)")
    private String assignedBy;

    @Column(name = "PA_ASGN_ON")
    private Date assignedOn;

    @Column(name = "PA_UTD_BY", columnDefinition = "nvarchar(100)")
    private String updatedBy;

    @Column(name = "PA_UTD_ON")
    private Date updatedOn;

}