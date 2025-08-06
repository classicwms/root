package com.tekclover.wms.api.idmaster.model.hhtuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `WH_ID`, `USR_ID`
 */
//@Table(
//		name = "tblhhtuser",
//		uniqueConstraints = {
//				@UniqueConstraint (
//						name = "unique_key_hhtuser",
//						columnNames = {"WH_ID", "USR_ID"})
//				}
//		)
@Table(name = "tblhhtuser")
public class HhtUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USR_ID", columnDefinition = "nvarchar(50)")
    private String userId;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(5)")
    private String languageId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
    private String plantId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Column(name = "LEVEL_ID")
    private Long levelId;

    @Column(name = "C_ID_DESC", columnDefinition = "nvarchar(200)")
    private String companyIdAndDescription;

    @Column(name = "PLANT_ID_DESC", columnDefinition = "nvarchar(200)")
    private String plantIdAndDescription;

    @Column(name = "WH_ID_DESC", columnDefinition = "nvarchar(200)")
    private String warehouseIdAndDescription;

    @Column(name = "LVL_ID_DESC", columnDefinition = "nvarchar(200)")
    private String levelIdAndDescription;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id", fetch = FetchType.EAGER)
    private Set<OrderTypeId> orderTypeIds;

    @Column(name = "PASSWORD", columnDefinition = "nvarchar(255)")
    private String password;

    @Column(name = "USER_NM", columnDefinition = "nvarchar(50)")
    private String userName;

    @Column(name = "STATUS_ID")
    private Long statusId;

    @Column(name = "CASE_RECEIPT")
    private Boolean caseReceipts;

    @Column(name = "ITEM_RECEIPT")
    private Boolean itemReceipts;

    @Column(name = "PUTAWAY")
    private Boolean putaway;

    @Column(name = "TRANSFER")
    private Boolean transfer;

    @Column(name = "PICKING")
    private Boolean picking;

    @Column(name = "QUALITY")
    private Boolean quality;


    @Column(name = "INVENTORY")
    private Boolean inventory;

    @Column(name = "CUSTOMER_RET")
    private Boolean customerReturn;

    @Column(name = "SUPPLIER_RET")
    private Boolean supplierReturn;

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

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(200)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY", columnDefinition = "nvarchar(200)")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn = new Date();

    //Additional Fields - HhtUser Picking
    @Column(name = "START_DATE")
    private Date startDate;

    @Column(name = "END_DATE")
    private Date endDate;

    @Column(name = "USR_PRESENT", columnDefinition = "nvarchar(200) default'1'")
    private String userPresent;

    @Column(name = "NO_OF_DAYS_LEAVE", columnDefinition = "nvarchar(100)")
    private String noOfDaysLeave;

    @Column(name = "TEAM_MEM_1", columnDefinition = "nvarchar(200)")
    private String teamMember1;

    @Column(name = "TEAM_MEM_2", columnDefinition = "nvarchar(200)")
    private String teamMember2;

    @Column(name = "TEAM_MEM_3", columnDefinition = "nvarchar(200)")
    private String teamMember3;

    @Column(name = "TEAM_MEM_4", columnDefinition = "nvarchar(200)")
    private String teamMember4;

    @Column(name = "TEAM_MEM_5", columnDefinition = "nvarchar(200)")
    private String teamMember5;



}
