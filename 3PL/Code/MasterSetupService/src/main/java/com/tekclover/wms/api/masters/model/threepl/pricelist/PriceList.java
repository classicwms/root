package com.tekclover.wms.api.masters.model.threepl.pricelist;

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
 * `LANG_ID`,`C_ID`, `PLANT_ID`, `WH_ID`,`MOD_ID`,`PRICE_LIST_ID`,'SER_TYP_ID',
 */
@Table(
        name = "tblpricelist",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_key_pricelist",
                        columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID","MOD_ID", "PRICE_LIST_ID","SER_TYP_ID","CHARGE_RANGE_ID"})
        }
)
@IdClass(PriceListCompositeKey.class)
public class PriceList {

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
    @Column(name = "MOD_ID",columnDefinition = "nvarchar(50)")
    private String moduleId;
    @Id
    @Column(name = "PRICE_LIST_ID")
    private Long priceListId;
    @Id
    @Column(name = "SER_TYP_ID")
    private Long serviceTypeId;
    @Id
    @Column(name="CHARGE_RANGE_ID")
    private Long chargeRangeId;

    @Column(name = "FROM_PERIOD")
    private Date fromPeriod;
    @Column(name="TO_PERIOD")
    private Date toPeriod;
    @Column(name = "CHARGE_RANGE_FROM")
    private Double chargeRangeFrom;
    @Column(name = "CHARGE_RANGE_TO")
    private Double chargeRangeTo;
    @Column(name="CHARGE_UNIT",columnDefinition = "nvarchar(20)")
    private String chargeUnit;
    @Column(name="PRICE_CHARGE_UNIT")
    private Double pricePerChargeUnit;
    @Column(name="PRICE_UNIT",columnDefinition = "nvarchar(20)")
    private String priceUnit;
    @Column(name="MIN_MONTH_PRICE")
    private Double minMonthlyPrice;
    @Column(name="STATUS_ID")
    private Long statusId;
    @Column(name="COMP_ID_DESC",columnDefinition = "nvarchar(200)")
    private String companyIdAndDescription;
    @Column(name="PLANT_ID_DESC",columnDefinition = "nvarchar(200)")
    private String plantIdAndDescription;
    @Column(name="WH_ID_DESC",columnDefinition = "nvarchar(200)")
    private String warehouseIdAndDescription;
    @Column(name="MOD_ID_DESC",columnDefinition = "nvarchar(200)")
    private String moduleIdAndDescription;
    @Column(name="SER_TYPE_ID_DESC",columnDefinition = "nvarchar(200)")
    private String serviceTypeIdAndDescription;
    @Column(name="DEC",columnDefinition = "nvarchar(200)")
    private String description;
    @Column(name="IS_DELETED")
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
