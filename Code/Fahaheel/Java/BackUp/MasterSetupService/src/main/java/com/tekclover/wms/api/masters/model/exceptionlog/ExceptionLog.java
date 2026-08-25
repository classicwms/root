package com.tekclover.wms.api.masters.model.exceptionlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblexceptionlog")
public class ExceptionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "ORDER_TYPE_ID", columnDefinition = "nvarchar(500)")
    private String orderTypeId;

    @Column(name = "ORDER_DATE")
    private Date orderDate;

    @Column(name = "ERR_MSG", columnDefinition = "nvarchar(3999)")
    private String errorMessage;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(100)")
    private String languageId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(100)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(100)")
    private String plantId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(100)")
    private String warehouseId;

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(200)")
    private String refDocNumber;

    @Column(name = "REF_FIELD_1", columnDefinition = "nvarchar(500)")
    private String referenceField1;

    @Column(name = "REF_FIELD_2", columnDefinition = "nvarchar(500)")
    private String referenceField2;

    @Column(name = "REF_FIELD_3", columnDefinition = "nvarchar(500)")
    private String referenceField3;

    @Column(name = "REF_FIELD_4", columnDefinition = "nvarchar(500)")
    private String referenceField4;

    @Column(name = "REF_FIELD_5", columnDefinition = "nvarchar(500)")
    private String referenceField5;

    @Column(name = "REF_FIELD_6", columnDefinition = "nvarchar(500)")
    private String referenceField6;

    @Column(name = "REF_FIELD_7", columnDefinition = "nvarchar(500)")
    private String referenceField7;

    @Column(name = "REF_FIELD_8", columnDefinition = "nvarchar(500)")
    private String referenceField8;

    @Column(name = "REF_FIELD_9", columnDefinition = "nvarchar(500)")
    private String referenceField9;

    @Column(name = "REF_FIELD_10", columnDefinition = "nvarchar(500)")
    private String referenceField10;

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn;

//    @Column(name = "UTD_BY")
//    private String updatedBy;
//
//    @Column(name = "UTD_ON")
//    private Date updatedOn;
}
