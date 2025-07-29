package com.tekclover.wms.api.idmaster.model.hhtuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblordertypeid")
@Where(clause = "IS_DELETED=0")
public class OrderTypeId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "ID")
    private Long id;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(10)")
    private String languageId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(25)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
    private String plantId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;

    @Column(name="OB_ORD_TYP_ID",columnDefinition = "nvarchar(50)")
    private String orderTypeId;

    @Column(name = "USR_ID",columnDefinition = "nvarchar(50)")
    private String userId;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator = 0L;

    @Column(name = "CTD_BY",columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn;

    @Column(name = "UTD_BY", columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn;
}
