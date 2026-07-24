package com.tekclover.wms.api.inbound.transaction.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "tblrequeststatus")
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_STATUS_ID")
    private Long requestStatusId;
    @Column(name = "REF_DOC_NO")
    private String refDocNo;
    @Column(name = "LANG_ID")
    private String languageId;
    @Column(name = "C_ID")
    private String companyCodeId;
    @Column(name = "PLANT_ID")
    private String plantId;
    @Column(name = "WH_ID")
    private String warehouseId;
    @Column(name = "TEXT")
    private String description;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "REF_TYPE")
    private String refType;
    @Column(name = "PROCESS_ID")
    private Long processId;
    @Column(name = "CTD_ON")
    private Date createdOn;
    @Column(name = "UTD_ON")
    private Date updatedOn;
}
