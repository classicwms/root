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
//    @Column(name = "company_code")
    private String companyCode;

//    @Column(name = "branch_code")
    private String branchCode;

    @NotBlank(message = "Partner Code is mandatory")
//    @Column(name = "partner_code")
    private String partnerCode;

    @NotBlank(message = "Partner Name is mandatory")
//    @Column(name = "partner_name")
    private String partnerName;

    @NotBlank(message = "Address1 is mandatory")
//    @Column(name = "address1")
    private String address1;

//    @Column(name = "address2")
    private String address2;

//    @Column(name = "phone_number")
    private String phoneNumber;

//    @Column(name = "civil_id")
    private String civilId;

//    @Column(name = "country")
    private String country;

//    @Column(name = "alternate_phone_number")
    private String alternatePhoneNumber;

    @NotBlank(message = "Created By is mandatory")
//    @Column(name = "created_by")
    private String createdBy;

    @NotBlank(message = "Created On Date is mandatory")
//    @Column(name = "created_on")
    private String createdOn;

//    @Column(name = "is_new")
    private String isNew;

//    @Column(name = "is_update")
    private String isUpdate;

//    @Column(name = "is_completed")
    private String isCompleted;

//    @Column(name = "updated_on")
    private Date updatedOn;

//    @Column(name = "remarks")
    private String remarks;

    //ProcessedStatusIdOrderByOrderReceivedOn

//    @Column(name = "processed_status_id")
    private Long processedStatusId = 0L;

//    @Column(name = "order_received_on")
    private Date orderReceivedOn;

//    @Column(name = "order_processed_on")
    private Date orderProcessedOn;

    //MiddleWare Fields
//    @Column(name = "middleware_id")
    private Long middlewareId;

//    @Column(name = "middleware_table")
    private String middlewareTable;
}
