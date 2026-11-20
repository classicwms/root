package com.tekclover.wms.api.idmaster.model.websocketnotification;

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
@Table(name = "tblwebsocketnotification")
@Where(clause = "status='false'")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id = 0L;

    @Column(name = "topic", columnDefinition = "nvarchar(255)")
    private String topic;

    @Column(name = "message", columnDefinition = "nvarchar(1999)")
    private String message;

    @Column(name = "user_id", columnDefinition = "nvarchar(50)")
    private String userId;

    @Column(name = "user_type", columnDefinition = "nvarchar(50)")
    private String userType;

    @Column(name = "status")
    private Boolean status = false;

    @Column(name = "IS_DELETED")
    private Boolean deletionIndicator = false;

    @Column(name = "CTD_BY", columnDefinition = "nvarchar(50)")
    private String createdBy;

    @Column(name = "CTD_ON")
    private Date createdOn = new Date();

    @Column(name = "UTD_BY", columnDefinition = "nvarchar(50)")
    private String updatedBy;

    @Column(name = "UTD_ON")
    private Date updatedOn = new Date();

    @Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(25)")
    private String documentNumber;

    @Column(name = "REF_NO", columnDefinition = "nvarchar(25)")
    private String referenceNumber;

    @Column(name = "C_ID", columnDefinition = "nvarchar(25)")
    private String companyCodeId;

    @Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
    private String plantId;

    @Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
    private String languageId;

    @Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
    private String warehouseId;

    @Column(name = "STORAGE_BIN", columnDefinition = "nvarchar(50)")
    private String storageBin;

    @Column(name = "NEW_CREATED")
    private Boolean newCreated = true;

}