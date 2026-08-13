package com.tekclover.wms.api.idmaster.model.notificationmessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblnotificationmessage")
public class NotificationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(50)")
    private String languageId;

    @Column(name = "C_ID", columnDefinition = "nvarchar(50)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
    private String plantId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
    private String warehouseId;

    @Column(name = "PROCESS_ID", columnDefinition = "nvarchar(50)")
    private String processId;

    @Column(name = "USER_ID", columnDefinition = "nvarchar(50)")
    private String userId;

    @Column(name = "TITLE", columnDefinition = "nvarchar(50)")
    private String title;

    @Column(name = "MSG", columnDefinition = "nvarchar(200)")
    private String message;

    @Column(name = "MENU")
    private Boolean menu = false;

    @Column(name = "TAB")
    private Boolean tab = false;

    @Column(name = "ORDER_TYPE", columnDefinition = "nvarchar(50)")
    private String orderType;

    @Column(name = "DOC_ID")
    private Long documentId = 0L;

    @Column(name = "IS_DELETED")
    private Long deletionIndicator = 0L;

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn = new Date();

    @Column(name = "USER_TYPE", columnDefinition = "nvarchar(50)")
    private String userType;

    @Column(name = "ST_BIN", columnDefinition = "nvarchar(50)")
    private String storageBin;

    @Column(name = "NEW_CREATE", columnDefinition = "bit default '1'")
    private boolean newCreated;

    @Column(name = "DOC_NO", columnDefinition = "nvarchar(50)")
    private String documentNumber;

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(50)")
    private String referenceNumber;

    @Column(name = "status")
    private Boolean status = false;


}