package com.almailem.ams.api.connector.model.master;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CUSTOMERMASTER")
public class CustomerMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Customermasterid")
    private Long customerMasterId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(50)", nullable = false)
    private String companyCode;

    @Column(name = "Branchcode", columnDefinition = "nvarchar(50)")
    private String branchCode;

    @NotBlank(message = "Customer Code is mandatory")
    @Column(name = "Customercode", columnDefinition = "nvarchar(50)", nullable = false)
    private String customerCode;

    @NotBlank(message = "Customer Name is mandatory")
    @Column(name = "Customername", columnDefinition = "nvarchar(50)", nullable = false)
    private String customerName;

    @NotBlank(message = "HomeAddress1 is mandatory")
    @Column(name = "Homeaddress1", columnDefinition = "nvarchar(3999)", nullable = false)
    private String homeAddress1;

    @Column(name = "Homeaddress2", columnDefinition = "nvarchar(3999)")
    private String homeAddress2;

    @Column(name = "Hometelnumber", columnDefinition = "nvarchar(500)")
    private String homeTelNumber;

    @Column(name = "Civilidnumber", columnDefinition = "nvarchar(200)")
    private String civilIdNumber;

    @Column(name = "Mobilenumber", columnDefinition = "nvarchar(200)")
    private String mobileNumber;

    @NotBlank(message = "Created Username is mandatory")
    @Column(name = "Createdusername", columnDefinition = "nvarchar(50)", nullable = false)
    private String createdUsername;

    @NotNull(message = "Customer Creation Date is mandatory")
    @Column(name = "Customercreationdate")
    private Date customerCreationDate;

    @Column(name = "Is_new", columnDefinition = "nvarchar(20)", nullable = false)
    private String isNew;

    @Column(name = "Is_update", columnDefinition = "nvarchar(20)", nullable = false)
    private String isUpdate;

    @Column(name = "Is_completed", columnDefinition = "nvarchar(10)")
    private String isCompleted;

    @Column(name = "Updatedon")
    private Date updatedOn;

    //ProcessedStatusIdOrderByOrderReceivedOn
    @Column(name = "Processedstatusid", columnDefinition = "bigint default'0'")
    private Long processedStatusId = 0L;

    @Column(name = "Orderreceivedon")
    private Date orderReceivedOn;

    @Column(name = "Orderprocessedon")
    private Date orderProcessedOn;
//    private String remarks;
}