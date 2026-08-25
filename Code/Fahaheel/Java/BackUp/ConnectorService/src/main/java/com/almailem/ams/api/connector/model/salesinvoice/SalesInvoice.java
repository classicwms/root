package com.almailem.ams.api.connector.model.salesinvoice;

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
@Table(name = "SALESINVOICE")
public class SalesInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Salesinvoiceid")
    private Long salesInvoiceId;

    @NotBlank(message = "Company Code is mandatory")
    @Column(name = "Companycode", columnDefinition = "nvarchar(50)", nullable = false)
    private String companyCode;

    @NotBlank(message = "Branch Code is mandatory")
    @Column(name = "Branchcode", columnDefinition = "nvarchar(50)", nullable = false)
    private String branchCode;

    @NotBlank(message = "Sales Invoice Number is mandatory")
    @Column(name = "Salesinvoicenumber", columnDefinition = "nvarchar(50)", nullable = false)
    private String salesInvoiceNumber;

//    @NotBlank(message = "Delivery Type is mandatory")
    @Column(name = "Deliverytype", columnDefinition = "nvarchar(50)")
    private String deliveryType;

    @NotBlank(message = "Sales Order Number is mandatory")
    @Column(name = "Salesordernumber", columnDefinition = "nvarchar(50)", nullable = false)
    private String salesOrderNumber;

    @NotBlank(message = "Pick List Number is mandatory")
    @Column(name = "Picklistnumber", columnDefinition = "nvarchar(50)", nullable = false)
    private String pickListNumber;

    @NotNull(message = "Invoice Date is mandatory")
    @Column(name = "Invoicedate")
    private Date invoiceDate;

    @NotBlank(message = "Customer Id is mandatory")
    @Column(name = "Customerid", columnDefinition = "nvarchar(50)", nullable = false)
    private String customerId;

    @NotBlank(message = "Customer Name is mandatory")
    @Column(name = "Customername", columnDefinition = "nvarchar(50)", nullable = false)
    private String customerName;

    @NotBlank(message = "Address is mandatory")
    @Column(name = "Address", columnDefinition = "nvarchar(500)", nullable = false)
    private String address;

    @NotBlank(message = "Phone Number is mandatory")
    @Column(name = "Phonenumber", columnDefinition = "nvarchar(50)", nullable = false)
    private String phoneNumber;

    @NotBlank(message = "Alternate No is mandatory")
    @Column(name = "Alternateno", columnDefinition = "nvarchar(50)", nullable = false)
    private String alternateNo;

    @Column(name = "Status", columnDefinition = "nvarchar(50)")
    private String status;

    //ProcessedStatusIdOrderByOrderReceivedOn
    @Column(name = "Processedstatusid", columnDefinition = "bigint default'0'")
    private Long processedStatusId = 0L;

    @Column(name = "Orderreceivedon")
    private Date orderReceivedOn;

    @Column(name = "Orderprocessedon")
    private Date orderProcessedOn;
}