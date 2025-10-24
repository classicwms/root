package com.tekclover.wms.api.masters.model.numberrangestoragebin;


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
        name = "tblnumberrangestoragebin",
        uniqueConstraints={
                @UniqueConstraint(
                        name = "unique_key_numberrangestoragebin",
                        columnNames ={ "C_ID","LANG_ID","PLANT_ID","WH_ID","FL_ID","ST_SEC_ID","ROW_ID","AISLE_ID"})
        }
)
@IdClass(NumberRangeStorageBinCompositeKey.class)
public class NumberRangeStorageBin {

        @Id
        @Column(name="C_ID",columnDefinition = "nvarchar(50)")
        private String companyCodeId;

        @Id
        @Column(name = "PLANT_ID",columnDefinition = "nvarchar(50)")
        private String plantId;

        @Id
        @Column(name="LANG_ID",columnDefinition = "nvarchar(5)")
        private String languageId;

        @Id
        @Column(name="WH_ID",columnDefinition = "nvarchar(50)")
        private String warehouseId;

        @Id
        @Column(name="FL_ID")
        private Long floorId;

        @Id
        @Column(name="ST_SEC_ID",columnDefinition ="nvarchar(50)")
        private String storageSectionId;

        @Id
        @Column(name="ROW_ID",columnDefinition = "nvarchar(50)")
        private String rowId;

        @Id
        @Column(name="AISLE_ID",columnDefinition = "nvarchar(50)")
        private String aisleNumber;

        @Column(name="SPAN_ID",columnDefinition = "nvarchar(50)")
        private String spanId;

        @Column(name="SHELF_ID",columnDefinition = "nvarchar(50)")
        private String shelfId;

        @Column(name="NUM_RAN_TYP",columnDefinition = "nvarchar(50)")
        private String numberRangeType;

        @Column(name="NUM_RAN_FROM",columnDefinition = "nvarchar(50)")
        private String numberRangeFrom;

        @Column(name="NUM_RAN_TO",columnDefinition = "nvarchar(50)")
        private String numberRangeTo;

        @Column(name="NUM_RAN_CURRENT",columnDefinition = "nvarchar(50)")
        private String currentNumberRange;

        @Column(name = "IS_DELETED")
        private Long deletionIndicator;

        @Column(name = "REF_FIELD_1",columnDefinition = "nvarchar(200)")
        private String referenceField1;

        @Column(name = "REF_FIELD_2",columnDefinition = "nvarchar(200)")
        private String referenceField2;

        @Column(name = "REF_FIELD_3",columnDefinition = "nvarchar(200)")
        private String referenceField3;

        @Column(name = "REF_FIELD_4",columnDefinition = "nvarchar(200)")
        private String referenceField4;

        @Column(name = "REF_FIELD_5",columnDefinition = "nvarchar(200)")
        private String referenceField5;

        @Column(name = "REF_FIELD_6",columnDefinition = "nvarchar(200)")
        private String referenceField6;

        @Column(name = "REF_FIELD_7",columnDefinition = "nvarchar(200)")
        private String referenceField7;

        @Column(name = "REF_FIELD_8",columnDefinition = "nvarchar(200)")
        private String referenceField8;

        @Column(name = "REF_FIELD_9",columnDefinition = "nvarchar(200)")
        private String referenceField9;

        @Column(name = "REF_FIELD_10",columnDefinition = "nvarchar(200)")
        private String referenceField10;


        @Column(name = "CTD_BY",columnDefinition = "nvarchar(50)")
        private String createdBy;

        @Column(name = "CTD_ON")
        private Date createdOn = new Date();

        @Column(name = "UTD_BY",columnDefinition = "nvarchar(50)")
        private String updatedBy;

        @Column(name = "UTD_ON")
        private Date updatedOn = new Date();
}
