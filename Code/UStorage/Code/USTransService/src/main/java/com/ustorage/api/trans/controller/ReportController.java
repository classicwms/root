package com.ustorage.api.trans.controller;

import java.text.ParseException;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.ustorage.api.trans.model.agreement.Agreement;
import com.ustorage.api.trans.model.quote.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ustorage.api.trans.model.impl.*;
import com.ustorage.api.trans.model.reports.*;

import com.ustorage.api.trans.service.ReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Report" }, value = "Report Operations related to ReportController")
@SwaggerDefinition(tags = { @Tag(name = "Report", description = "Operations related to Report") })
@RequestMapping("/report")
@RestController
public class ReportController {

	@Autowired
    ReportService reportService;

    //--------------------------------------------WorkOrderStatus------------------------------------------------------------------------

	@ApiOperation(response = Optional.class, value = "Report-Work_Order_Status_Report") // label for swagger
    @PostMapping("/work-order-status-report")
    public ResponseEntity<List<WorkOrderStatusReportImpl>> getWorkOrderStatus (@RequestBody WorkOrderStatus workOrderStatus)
            throws Exception {
        List<WorkOrderStatusReportImpl> data = reportService.getWorkOrderStatus(workOrderStatus);
        return new ResponseEntity<List<WorkOrderStatusReportImpl>>(data, HttpStatus.OK);
    }
    //--------------------------------------------Efficiency Record------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Report-Efficiency_Record") // label for swagger
    @PostMapping("/efficiency-record-report")
    public ResponseEntity<List<EfficiencyRecordImpl>> getEfficiencyRecord (@RequestBody EfficiencyRecord efficiencyRecord)
            throws Exception {
        List<EfficiencyRecordImpl> data = reportService.getEfficiencyRecord(efficiencyRecord);
        return new ResponseEntity<List<EfficiencyRecordImpl>>(data, HttpStatus.OK);
    }
    //--------------------------------------------Quotation Status------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Report-Quotation_Status") // label for swagger
    @PostMapping("/quotation-status-report")
    public ResponseEntity<List<QuotationStatusImpl>> getQuotationStatus (@RequestBody QuotationStatus quotationStatus)
            throws Exception {
        List<QuotationStatusImpl> data = reportService.getQuotationStatus(quotationStatus);
        return new ResponseEntity<List<QuotationStatusImpl>>(data, HttpStatus.OK);
    }
    //--------------------------------------------EnquiryStatus------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Report-Enquiry_Status") // label for swagger
    @PostMapping("/enquiry-status-report")
    public ResponseEntity<List<EnquiryStatusImpl>> getEnquiryStatus (@RequestBody EnquiryStatus enquiryStatus)
            throws Exception {
        List<EnquiryStatusImpl> data = reportService.getEnquiryStatus(enquiryStatus);
        return new ResponseEntity<List<EnquiryStatusImpl>>(data, HttpStatus.OK);
    }
    //--------------------------------------------Fillrate Status------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Report-Fillrate_Status") // label for swagger
    @PostMapping("/fillrate-status-report")
    public ResponseEntity<List<FillrateStatusImpl>> getFillrateStatus (@RequestBody FillrateStatus fillrateStatus)
            throws Exception {
        List<FillrateStatusImpl> data = reportService.getFillrateStatus(fillrateStatus);
        return new ResponseEntity<List<FillrateStatusImpl>>(data, HttpStatus.OK);
    }

    //--------------------------------------------Contract Renewal Status------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Report-Contract_Renewal_Status") // label for swagger
    @PostMapping("/contract-renewal-status-report")
    public ResponseEntity<List<ContractRenewalStatusImpl>> getContractRenewalStatus (@RequestBody ContractRenewalStatus contractRenewalStatus)
            throws Exception {
        List<ContractRenewalStatusImpl> data = reportService.getContractRenewalStatus(contractRenewalStatus);
        return new ResponseEntity<List<ContractRenewalStatusImpl>>(data, HttpStatus.OK);
    }

    //--------------------------------------------Payment Due Status------------------------------------------------------------------------
//    @ApiOperation(response = Optional.class, value = "Report-Payment_Due_Status") // label for swagger
//    @PostMapping("/payment-due-status-report")
//    public ResponseEntity<List<PaymentDueStatusReportImpl>> getPaymentDueStatus (@RequestBody PaymentDueStatus paymentDueStatus)
//            throws Exception {
//        List<PaymentDueStatusReportImpl> data = reportService.getPaymentDueStatus(paymentDueStatus);
//        return new ResponseEntity<List<PaymentDueStatusReportImpl>>(data, HttpStatus.OK);
//    }

    //--------------------------------------------Payment Due ------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Report-Payment_Due") // label for swagger
    @PostMapping("/payment-due-report")
    public ResponseEntity<?> getPaymentDue (@RequestBody PaymentDueStatus paymentDueStatus)
            throws Exception {
        PaymentDueStatusReport data = reportService.getPaymentDue(paymentDueStatus);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    //--------------------------------------------Document Status Report------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Report-Document_Status") // label for swagger
    @PostMapping("/document-status-report")
    public ResponseEntity<?> getDocumentStatus (@RequestBody DocumentStatusInput documentStatusInput)
            throws Exception {
        DocumentStatus data = reportService.getDocumentStatus(documentStatusInput);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    //--------------------------------------------Customer Drop down------------------------------------------------------------------------
    @ApiOperation(response = CustomerDropDown.class, value = "Get a Customer Dropdown") // label for swagger
    @GetMapping("/dropdown/customerName")
    public ResponseEntity<?> getCustomerDropdownList() {
        CustomerDropdownList matterGenAcc = reportService.getCustomerDropdownList();
        return new ResponseEntity<>(matterGenAcc, HttpStatus.OK);
    }

    //--------------------------------------------Storage Drop down------------------------------------------------------------------------
    @ApiOperation(response = StorageDropDown.class, value = "Get a Storage Dropdown") // label for swagger
    @GetMapping("/dropdown/storageUnit")
    public ResponseEntity<?> getStorageDropdownList() {
        StorageDropdownList storageUnitDropdown = reportService.getStorageDropdownList();
        return new ResponseEntity<>(storageUnitDropdown, HttpStatus.OK);
    }

    //-----------------------------------------Customer Details-----------------------------------------------------------
    @ApiOperation(response = Dropdown.class, value = "Customer Details") // label for swagger
    @PostMapping("/customerDetail")
    public ResponseEntity<?> postCustomerDetail(@Valid @RequestBody CustomerDetailInput customerDetailInput)
            throws Exception {
        Dropdown createdDetails = reportService.getDropdownList(customerDetailInput);
        return new ResponseEntity<>(createdDetails , HttpStatus.OK);
    }

    //--------------------------------------------DashBoard------------------------------------------------------------------------
    //--------------------------------------------Billed Paid------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Get Billed & Paid Amount MonthWise") // label for swagger
    @GetMapping("/dashboard/billedAndPaidAmount/{year}")
    public ResponseEntity<?> getBilledPaidAmount(@PathVariable Year year) throws Exception {
        BilledPaid billedPaid = reportService.getBilledPaid(year);
        return new ResponseEntity<>(billedPaid, HttpStatus.OK);
    }

    //--------------------------------------------Lead And Customer------------------------------------------------------------------------
    @ApiOperation(response = Optional.class, value = "Get Lead and Customer Count MonthWise") // label for swagger
    @GetMapping("/dashboard/leadAndCustomer/{year}")
    public ResponseEntity<?> getLeadAndCustomer(@PathVariable Year year) throws ParseException{
        LeadAndCustomer leadAndCustomer = reportService.getLeadAndCustomer(year);
        return new ResponseEntity<>(leadAndCustomer, HttpStatus.OK);
    }

}
