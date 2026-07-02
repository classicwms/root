package com.tekclover.wms.api.masters.model.masters;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Entity
@Table(name = "tblordercustomermaster")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customer_id;

    @NotBlank(message = "Company Code is mandatory")
    private String companyCode;

    private String branchCode;

    @NotBlank(message = "Partner Code is mandatory")
    private String partnerCode;

    @NotBlank(message = "Partner Name is mandatory")
    private String partnerName;

    @NotBlank(message = "Address1 is mandatory")
    private String address1;

    private String address2;

    private String phoneNumber;

    private String civilId;

    private String country;

    private String alternatePhoneNumber;

    @NotBlank(message = "Created By is mandatory")
    private String createdBy;

    @NotBlank(message = "Created On Date is mandatory")
    private String createdOn;

    private String isNew;
    private String isUpdate;
    private String isCompleted;
    private Date updatedOn;
    private String remarks;

    //ProcessedStatusIdOrderByOrderReceivedOn

    private Long processedStatusId = 0L;
    private Date orderReceivedOn;
    private Date orderProcessedOn;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
}
