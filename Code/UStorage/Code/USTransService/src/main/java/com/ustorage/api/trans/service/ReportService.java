package com.ustorage.api.trans.service;

import com.ustorage.api.trans.model.agreement.Agreement;
import com.ustorage.api.trans.model.leadcustomer.LeadCustomer;
import com.ustorage.api.trans.repository.AgreementRepository;
import com.ustorage.api.trans.repository.LeadCustomerRepository;
import com.ustorage.api.trans.repository.ReportRepository;

import com.ustorage.api.trans.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.trans.model.impl.*;
import com.ustorage.api.trans.model.reports.*;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportService {
	@Autowired
	private LeadCustomerRepository leadCustomerRepository;

	@Autowired
	private ReportRepository reportRepository;
	@Autowired
	private AgreementRepository agreementRepository;

	//--------------------------------------------WorkOrderStatus------------------------------------------------------------------------

	public List<WorkOrderStatusReportImpl> getWorkOrderStatus(WorkOrderStatus workOrderStatus) throws ParseException {

		if (workOrderStatus.getStartDate() != null &&
				workOrderStatus.getEndDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(workOrderStatus.getStartDate(),
					workOrderStatus.getEndDate());
			workOrderStatus.setStartDate(dates[0]);
			workOrderStatus.setEndDate(dates[1]);
		}

		List<WorkOrderStatusReportImpl> data = reportRepository.getWorkOrderStatus(workOrderStatus.getWorkOrderId(),
				workOrderStatus.getWorkOrderSbu(),
				workOrderStatus.getStartDate(),
				workOrderStatus.getEndDate());
		return data;
	}
	//--------------------------------------------Efficiency Record------------------------------------------------------------------------
	public List<EfficiencyRecordImpl> getEfficiencyRecord(EfficiencyRecord efficiencyRecord) throws ParseException {

		if (efficiencyRecord.getStartDate() != null &&
				efficiencyRecord.getEndDate() != null) {
			Date[] dates = DateUtils.addTimeToDatesForSearch(efficiencyRecord.getStartDate(),
					efficiencyRecord.getEndDate());
			efficiencyRecord.setStartDate(dates[0]);
			efficiencyRecord.setEndDate(dates[1]);
		}

		List<EfficiencyRecordImpl> data = reportRepository.getEfficiencyRecord(efficiencyRecord.getStartDate(), efficiencyRecord.getEndDate(),
				efficiencyRecord.getJobCardType(),
				efficiencyRecord.getProcessedBy());
		return data;
	}
	//--------------------------------------------Quotation Status------------------------------------------------------------------------
	public List<QuotationStatusImpl> getQuotationStatus(QuotationStatus quotationStatus) throws ParseException {

		List<QuotationStatusImpl> data = reportRepository.getQuotationStatus(quotationStatus.getQuoteId(),
				quotationStatus.getStatus(),
				quotationStatus.getRequirementType(),
				quotationStatus.getSbu());
		return data;
	}
	//--------------------------------------------EnquiryStatus------------------------------------------------------------------------
	public List<EnquiryStatusImpl> getEnquiryStatus(EnquiryStatus enquiryStatus) throws ParseException {

		List<EnquiryStatusImpl> data = reportRepository.getEnquiryStatus(enquiryStatus.getEnquiryId(),
				enquiryStatus.getEnquiryStatus(),
				enquiryStatus.getRequirementType(),
				enquiryStatus.getSbu());
		return data;
	}
	//--------------------------------------------Fillrate Status------------------------------------------------------------------------
	public List<FillrateStatusImpl> getFillrateStatus(FillrateStatus fillrateStatus) throws ParseException {

		List<FillrateStatusImpl> data = reportRepository.getFillrateStatus(fillrateStatus.getPhase(),
				fillrateStatus.getStoreNumber(),
				fillrateStatus.getStorageType(),
				fillrateStatus.getStatus());
		return data;
	}

	//--------------------------------------------Contract Renewal Status------------------------------------------------------------------------
	public List<ContractRenewalStatusImpl> getContractRenewalStatus(ContractRenewalStatus contractRenewalStatus) throws ParseException {

		List<ContractRenewalStatusImpl> data = reportRepository.getContractRenewalStatus(contractRenewalStatus.getPhase(),
				contractRenewalStatus.getStoreNumber(),
				contractRenewalStatus.getStorageType());
		return data;
	}

	//--------------------------------------------Payment Due Status------------------------------------------------------------------------
	public List<PaymentDueStatusReportImpl> getPaymentDueStatus(PaymentDueStatus paymentDueStatus) throws ParseException {

		List<PaymentDueStatusReportImpl> data = reportRepository.getPaymentDueStatus(paymentDueStatus.getAgreementNumber(),
				paymentDueStatus.getCustomerName(),
				paymentDueStatus.getCustomerCode(),
				paymentDueStatus.getPhoneNumber(),
				paymentDueStatus.getSecondaryNumber(),
				paymentDueStatus.getCivilId(),
				paymentDueStatus.getStartDate(),
				paymentDueStatus.getEndDate());
		return data;
	}

	//--------------------------------------------Document Status Report------------------------------------------------------------------------
	public DocumentStatus getDocumentStatus(DocumentStatusInput documentStatusInput) throws ParseException {

		DocumentStatus documentStatus = new DocumentStatus();

		if(documentStatusInput.getCustomerCode()==null || documentStatusInput.getCustomerCode().isEmpty()){
			documentStatusInput.setCustomerCode(null);
		}
		if(documentStatusInput.getDocumentType()==null || documentStatusInput.getDocumentType().isEmpty()){
			documentStatusInput.setDocumentType(null);
		}

		List<DocumentStatusImpl> data1 = reportRepository.getAgreemnt(documentStatusInput.getCustomerCode(),documentStatusInput.getStartDate(),documentStatusInput.getEndDate());
		List<DocumentStatusKey> agreementList = new ArrayList<>();
		for (DocumentStatusImpl idocumentStatus : data1) {
			DocumentStatusKey documentStatusKey = new DocumentStatusKey();
			documentStatusKey.setDocumentNumber(idocumentStatus.getDocumentNumber());
			documentStatusKey.setAmount(idocumentStatus.getAmount());
			documentStatusKey.setStatus(idocumentStatus.getStatus());
			documentStatusKey.setRemark(idocumentStatus.getRemark());
			documentStatusKey.setCode(idocumentStatus.getCode());
			documentStatusKey.setNote(idocumentStatus.getNote());
			documentStatusKey.setLocation(idocumentStatus.getLocation());
			documentStatusKey.setDocumentDate(idocumentStatus.getDocumentDate());
			documentStatusKey.setStartDate(idocumentStatus.getStartDate());
			documentStatusKey.setEndDate(idocumentStatus.getEndDate());
			documentStatusKey.setCustomerName(idocumentStatus.getCustomerName());
			documentStatusKey.setEmail(idocumentStatus.getEmail());
			documentStatusKey.setMobile(idocumentStatus.getMobile());
			documentStatusKey.setPhone(idocumentStatus.getPhone());
			documentStatusKey.setCivilId(idocumentStatus.getCivilId());
			documentStatusKey.setCustomerId(idocumentStatus.getCustomerId());
			documentStatusKey.setStoreNumber(idocumentStatus.getStoreNumber());
			documentStatusKey.setDocumentType("Agreement");

			agreementList.add(documentStatusKey);
		}

		if(documentStatusInput.getCustomerCode()==null&&documentStatusInput.getDocumentType()==null&&
			documentStatusInput.getStartDate()==null&&documentStatusInput.getEndDate()==null){

			documentStatus.setAgreement(agreementList);

		}else if(documentStatusInput.getStartDate()!=null||documentStatusInput.getEndDate()!=null||
				documentStatusInput.getCustomerCode()!=null){

			documentStatus.setAgreement(agreementList);

		}else{

			documentStatus.setAgreement(new ArrayList<>());
		}

		List<DocumentStatusImpl> data2 = reportRepository.getInvice(documentStatusInput.getCustomerCode(),documentStatusInput.getStartDate(),documentStatusInput.getEndDate());
		List<DocumentStatusKey> invoiceList = new ArrayList<>();
		for (DocumentStatusImpl idocumentStatus : data2) {
			DocumentStatusKey documentStatusKey = new DocumentStatusKey();
			documentStatusKey.setDocumentNumber(idocumentStatus.getDocumentNumber());
			documentStatusKey.setStoreNumber(idocumentStatus.getStoreNumber());
			documentStatusKey.setAmount(idocumentStatus.getAmount());
			documentStatusKey.setStatus(idocumentStatus.getStatus());
			documentStatusKey.setRemark(idocumentStatus.getRemark());
			documentStatusKey.setCode(idocumentStatus.getCode());
			documentStatusKey.setNote(idocumentStatus.getNote());
			documentStatusKey.setLocation(idocumentStatus.getLocation());
			documentStatusKey.setDocumentDate(idocumentStatus.getDocumentDate());
			documentStatusKey.setStartDate(idocumentStatus.getStartDate());
			documentStatusKey.setEndDate(idocumentStatus.getEndDate());
			documentStatusKey.setCustomerName(idocumentStatus.getCustomerName());
			documentStatusKey.setEmail(idocumentStatus.getEmail());
			documentStatusKey.setMobile(idocumentStatus.getMobile());
			documentStatusKey.setPhone(idocumentStatus.getPhone());
			documentStatusKey.setCivilId(idocumentStatus.getCivilId());
			documentStatusKey.setCustomerId(idocumentStatus.getCustomerId());
			documentStatusKey.setDocumentType("Invoice");

			invoiceList.add(documentStatusKey);
		}

		if(documentStatusInput.getCustomerCode()==null&&documentStatusInput.getDocumentType()==null&&
				documentStatusInput.getStartDate()==null&&documentStatusInput.getEndDate()==null){

			documentStatus.setInvoice(invoiceList);

		}else if(documentStatusInput.getStartDate()!=null||documentStatusInput.getEndDate()!=null||
				documentStatusInput.getCustomerCode()!=null){

			documentStatus.setInvoice(invoiceList);

		}else{

			documentStatus.setInvoice(new ArrayList<>());

		}

		List<DocumentStatusImpl> data3 = reportRepository.getPaymnt(documentStatusInput.getCustomerCode(),documentStatusInput.getStartDate(),documentStatusInput.getEndDate());
		List<DocumentStatusKey> paymentList = new ArrayList<>();
		for (DocumentStatusImpl idocumentStatus : data3) {
			DocumentStatusKey documentStatusKey = new DocumentStatusKey();
			documentStatusKey.setDocumentNumber(idocumentStatus.getDocumentNumber());
			documentStatusKey.setStoreNumber(idocumentStatus.getStoreNumber());
			documentStatusKey.setAmount(idocumentStatus.getAmount());
			documentStatusKey.setStatus(idocumentStatus.getStatus());
			documentStatusKey.setRemark(idocumentStatus.getRemark());
			documentStatusKey.setCode(idocumentStatus.getCode());
			documentStatusKey.setNote(idocumentStatus.getNote());
			documentStatusKey.setLocation(idocumentStatus.getLocation());
			documentStatusKey.setDocumentDate(idocumentStatus.getDocumentDate());
			documentStatusKey.setStartDate(idocumentStatus.getStartDate());
			documentStatusKey.setEndDate(idocumentStatus.getEndDate());
			documentStatusKey.setCustomerName(idocumentStatus.getCustomerName());
			documentStatusKey.setEmail(idocumentStatus.getEmail());
			documentStatusKey.setMobile(idocumentStatus.getMobile());
			documentStatusKey.setCustomerId(idocumentStatus.getCustomerId());
			documentStatusKey.setDocumentType("Payment");

			paymentList.add(documentStatusKey);
		}

		if(documentStatusInput.getCustomerCode()==null&&documentStatusInput.getDocumentType()==null&&
				documentStatusInput.getStartDate()==null&&documentStatusInput.getEndDate()==null){

			documentStatus.setPayment(paymentList);

		}else if(documentStatusInput.getStartDate()!=null||documentStatusInput.getEndDate()!=null||
				documentStatusInput.getCustomerCode()!=null){

			documentStatus.setPayment(paymentList);

		}else{

			documentStatus.setPayment(new ArrayList<>());

		}

		List<DocumentStatusImpl> data4 = reportRepository.getWorkordr(documentStatusInput.getCustomerCode(),documentStatusInput.getStartDate(),documentStatusInput.getEndDate());
		List<DocumentStatusKey> workorderList = new ArrayList<>();
		for (DocumentStatusImpl idocumentStatus : data4) {
			DocumentStatusKey documentStatusKey = new DocumentStatusKey();
			documentStatusKey.setDocumentNumber(idocumentStatus.getDocumentNumber());
			documentStatusKey.setAmount(idocumentStatus.getAmount());
			documentStatusKey.setStatus(idocumentStatus.getStatus());
			documentStatusKey.setRemark(idocumentStatus.getRemark());
			documentStatusKey.setCode(idocumentStatus.getCode());
			documentStatusKey.setNote(idocumentStatus.getNote());
			documentStatusKey.setLocation(idocumentStatus.getLocation());
			documentStatusKey.setDocumentDate(idocumentStatus.getDocumentDate());
			documentStatusKey.setStartDate(idocumentStatus.getStartDate());
			documentStatusKey.setEndDate(idocumentStatus.getEndDate());
			documentStatusKey.setCustomerName(idocumentStatus.getCustomerName());
			documentStatusKey.setEmail(idocumentStatus.getEmail());
			documentStatusKey.setMobile(idocumentStatus.getMobile());
			documentStatusKey.setPhone(idocumentStatus.getPhone());
			documentStatusKey.setCivilId(idocumentStatus.getCivilId());
			documentStatusKey.setCustomerId(idocumentStatus.getCustomerId());
			documentStatusKey.setDocumentType("WorkOrder");

			workorderList.add(documentStatusKey);
		}

		if(documentStatusInput.getCustomerCode()==null&&documentStatusInput.getDocumentType()==null&&
				documentStatusInput.getStartDate()==null&&documentStatusInput.getEndDate()==null){

			documentStatus.setWorkorder(workorderList);

		}else if(documentStatusInput.getStartDate()!=null||documentStatusInput.getEndDate()!=null||
				documentStatusInput.getCustomerCode()!=null){
			documentStatus.setWorkorder(workorderList);
		}else{

			documentStatus.setWorkorder(new ArrayList<>());

		}

		List<DocumentStatusImpl> data5 = reportRepository.getQote(documentStatusInput.getCustomerCode(),documentStatusInput.getStartDate(),documentStatusInput.getEndDate());
		List<DocumentStatusKey> quoteList = new ArrayList<>();
		for (DocumentStatusImpl idocumentStatus : data5) {
			DocumentStatusKey documentStatusKey = new DocumentStatusKey();
			documentStatusKey.setDocumentNumber(idocumentStatus.getDocumentNumber());
			documentStatusKey.setAmount(idocumentStatus.getAmount());
			documentStatusKey.setStatus(idocumentStatus.getStatus());
			documentStatusKey.setRemark(idocumentStatus.getRemark());
			documentStatusKey.setCode(idocumentStatus.getCode());
			documentStatusKey.setNote(idocumentStatus.getNote());
			documentStatusKey.setLocation(idocumentStatus.getLocation());
			documentStatusKey.setDocumentDate(idocumentStatus.getDocumentDate());
			documentStatusKey.setStartDate(idocumentStatus.getStartDate());
			documentStatusKey.setEndDate(idocumentStatus.getEndDate());
			documentStatusKey.setServiceType(idocumentStatus.getServiceType());
			documentStatusKey.setCustomerName(idocumentStatus.getCustomerName());
			documentStatusKey.setEmail(idocumentStatus.getEmail());
			documentStatusKey.setMobile(idocumentStatus.getMobile());
			documentStatusKey.setPhone(idocumentStatus.getPhone());
			documentStatusKey.setCustomerId(idocumentStatus.getCustomerId());
			documentStatusKey.setDocumentType("Quotation");

			quoteList.add(documentStatusKey);
		}

		if(documentStatusInput.getCustomerCode()==null&&documentStatusInput.getDocumentType()==null&&
				documentStatusInput.getStartDate()==null&&documentStatusInput.getEndDate()==null){

			documentStatus.setQuote(quoteList);

		}else if(documentStatusInput.getStartDate()!=null||documentStatusInput.getEndDate()!=null||
				documentStatusInput.getCustomerCode()!=null){
			documentStatus.setQuote(quoteList);
		}else{

			documentStatus.setQuote(new ArrayList<>());

		}

		List<DocumentStatusImpl> data6 = reportRepository.getEnqury(documentStatusInput.getCustomerCode(),documentStatusInput.getStartDate(),documentStatusInput.getEndDate());
		List<DocumentStatusKey> enquiryList = new ArrayList<>();
		for (DocumentStatusImpl idocumentStatus : data6) {
			DocumentStatusKey documentStatusKey = new DocumentStatusKey();
			documentStatusKey.setDocumentNumber(idocumentStatus.getDocumentNumber());
			documentStatusKey.setAmount(idocumentStatus.getAmount());
			documentStatusKey.setStatus(idocumentStatus.getStatus());
			documentStatusKey.setRemark(idocumentStatus.getRemark());
			documentStatusKey.setCode(idocumentStatus.getCode());
			documentStatusKey.setNote(idocumentStatus.getNote());
			documentStatusKey.setLocation(idocumentStatus.getLocation());
			documentStatusKey.setDocumentDate(idocumentStatus.getDocumentDate());
			documentStatusKey.setStartDate(idocumentStatus.getStartDate());
			documentStatusKey.setEndDate(idocumentStatus.getEndDate());
			documentStatusKey.setServiceType(idocumentStatus.getServiceType());
			documentStatusKey.setCustomerName(idocumentStatus.getCustomerName());
			documentStatusKey.setEmail(idocumentStatus.getEmail());
			documentStatusKey.setMobile(idocumentStatus.getMobile());
			documentStatusKey.setPhone(idocumentStatus.getPhone());
			documentStatusKey.setCivilId(idocumentStatus.getCivilId());
			documentStatusKey.setCustomerId(idocumentStatus.getCustomerId());
			documentStatusKey.setDocumentType("Enquiry");

			enquiryList.add(documentStatusKey);
		}

		if(documentStatusInput.getCustomerCode()==null&&documentStatusInput.getDocumentType()==null&&
				documentStatusInput.getStartDate()==null&&documentStatusInput.getEndDate()==null){

			documentStatus.setEnquiry(enquiryList);

		}else if(documentStatusInput.getStartDate()!=null||documentStatusInput.getEndDate()!=null||
				documentStatusInput.getCustomerCode()!=null){
			documentStatus.setEnquiry(enquiryList);
		}else{

			documentStatus.setEnquiry(new ArrayList<>());

		}

		if(documentStatusInput.getDocumentType()!=null){

			documentStatus.setAgreement(new ArrayList<>());
			documentStatus.setInvoice(new ArrayList<>());
			documentStatus.setPayment(new ArrayList<>());
			documentStatus.setWorkorder(new ArrayList<>());
			documentStatus.setQuote(new ArrayList<>());
			documentStatus.setEnquiry(new ArrayList<>());

			for(String documentType : documentStatusInput.getDocumentType()){
				if(documentType.equalsIgnoreCase("agreement")){
					documentStatus.setAgreement(agreementList);
				}
				if(documentType.equalsIgnoreCase("invoice")){
					documentStatus.setInvoice(invoiceList);
				}
				if(documentType.equalsIgnoreCase("payment")){
					documentStatus.setPayment(paymentList);
				}
				if(documentType.equalsIgnoreCase("workorder")){
					documentStatus.setWorkorder(workorderList);
				}
				if(documentType.equalsIgnoreCase("quotation")){
					documentStatus.setQuote(quoteList);
				}
				if(documentType.equalsIgnoreCase("enquiry")){
					documentStatus.setEnquiry(enquiryList);
				}
			}

}
		return documentStatus;

	}
//--------------------------------------------customer dropdown------------------------------------------------------------------------
	/*
	 * @return
	 */
	public CustomerDropdownList getCustomerDropdownList () {
		CustomerDropdownList dropdown = new CustomerDropdownList();

		List<ICustomerDropDown> customerDropDownList = reportRepository.getCustomerDropDownList();
		List<CustomerDropDown> customerDropDowns = new ArrayList<>();
		for (ICustomerDropDown iCustomerDropDown : customerDropDownList) {
			CustomerDropDown customerDropDown = new CustomerDropDown();
			customerDropDown.setCustomerCode(iCustomerDropDown.getCustomerCode());
			customerDropDown.setCustomerName(iCustomerDropDown.getCustomerName());
			customerDropDown.setCivilId(iCustomerDropDown.getCivilId());
			customerDropDowns.add(customerDropDown);
		}
		dropdown.setCustomerDropDown(customerDropDowns);
		return dropdown;
	}

	//--------------------------------------------storage unit dropdown------------------------------------------------------------------------
	/*
	 * @return
	 */
	public StorageDropdownList getStorageDropdownList () {
		StorageDropdownList sdropdown = new StorageDropdownList();

		List<IStorageDropDown> storageDropDownList = reportRepository.getStorageDropDownList();
		List<StorageDropDown> storageDropDowns = new ArrayList<>();
		for (IStorageDropDown iStorageDropDown : storageDropDownList) {
			StorageDropDown storageDropDown = new StorageDropDown();
			storageDropDown.setStoreNumber(iStorageDropDown.getStoreNumber());
			storageDropDown.setStoreId(iStorageDropDown.getStoreId());
			storageDropDown.setDescription(iStorageDropDown.getDescription());
			storageDropDowns.add(storageDropDown);
		}
		sdropdown.setStorageDropDown(storageDropDowns);
		return sdropdown;
	}
	//--------------------------------------------customer detail------------------------------------------------------------------------
	public Dropdown getDropdownList(CustomerDetailInput customerDetailInput) {
		Dropdown dropdown = new Dropdown();

		List<IKeyValuePair> ikeyValues = reportRepository.getClientNameList(customerDetailInput.getCustomerCode());
		List<KeyValuePair> clientList = new ArrayList<>();
		for (IKeyValuePair iKeyValuePair : ikeyValues) {
			KeyValuePair keyValuePair = new KeyValuePair();
			keyValuePair.setCustomerCode(iKeyValuePair.getDocumentNumber());
			keyValuePair.setCustomerName(iKeyValuePair.getNotes());
			clientList.add(keyValuePair);
		}
		dropdown.setCustomer(clientList);

		ikeyValues = reportRepository.getAgreement(customerDetailInput.getCustomerCode());
		List<KeyValue> agreementList = new ArrayList<>();
		for (IKeyValuePair iKeyValuePair : ikeyValues) {
			KeyValue agreementKey = new KeyValue();
			agreementKey.setDocumentNumber(iKeyValuePair.getDocumentNumber());
			agreementKey.setTotal(iKeyValuePair.getTotal());
			agreementKey.setStatus(iKeyValuePair.getStatus());
			agreementKey.setNotes(iKeyValuePair.getNotes());
			agreementKey.setDocumentDate(iKeyValuePair.getDocumentDate());
			agreementKey.setDocumentType("Agreement");
			agreementList.add(agreementKey);
		}
		dropdown.setAgreement(agreementList);

		ikeyValues = reportRepository.getInvoice(customerDetailInput.getCustomerCode());
		List<KeyValue> invoiceList = new ArrayList<>();
		for (IKeyValuePair iKeyValuePair : ikeyValues) {
			KeyValue invoiceKey = new KeyValue();
			invoiceKey.setDocumentNumber(iKeyValuePair.getDocumentNumber());
			invoiceKey.setTotal(iKeyValuePair.getTotal());
			invoiceKey.setStatus(iKeyValuePair.getStatus());
			invoiceKey.setNotes(iKeyValuePair.getNotes());
			invoiceKey.setDocumentDate(iKeyValuePair.getDocumentDate());
			invoiceKey.setDocumentType("Invoice");
			invoiceList.add(invoiceKey);
		}
		dropdown.setInvoice(invoiceList);

		ikeyValues = reportRepository.getPayment(customerDetailInput.getCustomerCode());
		List<KeyValue> paymentList = new ArrayList<>();
		for (IKeyValuePair iKeyValuePair : ikeyValues) {
			KeyValue paymentKey = new KeyValue();
			paymentKey.setDocumentNumber(iKeyValuePair.getDocumentNumber());
			paymentKey.setTotal(iKeyValuePair.getTotal());
			paymentKey.setStatus(iKeyValuePair.getStatus());
			paymentKey.setNotes(iKeyValuePair.getNotes());
			paymentKey.setDocumentDate(iKeyValuePair.getDocumentDate());
			paymentKey.setDocumentType("Payment");
			paymentList.add(paymentKey);
		}
		dropdown.setPayment(paymentList);

		ikeyValues = reportRepository.getWorkorder(customerDetailInput.getCustomerCode());
		List<KeyValue> workorderList = new ArrayList<>();
		for (IKeyValuePair iKeyValuePair : ikeyValues) {
			KeyValue workorderKey = new KeyValue();
			workorderKey.setDocumentNumber(iKeyValuePair.getDocumentNumber());
			workorderKey.setTotal(iKeyValuePair.getTotal());
			workorderKey.setStatus(iKeyValuePair.getStatus());
			workorderKey.setNotes(iKeyValuePair.getNotes());
			workorderKey.setDocumentDate(iKeyValuePair.getDocumentDate());
			workorderKey.setDocumentType("WorkOrder");
			workorderList.add(workorderKey);
		}
		dropdown.setWorkorder(workorderList);

		ikeyValues = reportRepository.getQuote(customerDetailInput.getCustomerCode());
		List<KeyValue> quoteList = new ArrayList<>();
		for (IKeyValuePair iKeyValuePair : ikeyValues) {
			KeyValue quoteKey = new KeyValue();
			quoteKey.setDocumentNumber(iKeyValuePair.getDocumentNumber());
			quoteKey.setTotal(iKeyValuePair.getTotal());
			quoteKey.setStatus(iKeyValuePair.getStatus());
			quoteKey.setNotes(iKeyValuePair.getNotes());
			quoteKey.setDocumentDate(iKeyValuePair.getDocumentDate());
			quoteKey.setDocumentType("Quotation");
			quoteList.add(quoteKey);
		}
		dropdown.setQuote(quoteList);

		ikeyValues = reportRepository.getEnquiry(customerDetailInput.getCustomerCode());
		List<KeyValue> enquiryList = new ArrayList<>();
		for (IKeyValuePair iKeyValuePair : ikeyValues) {
			KeyValue enquiryKey = new KeyValue();
			enquiryKey.setDocumentNumber(iKeyValuePair.getDocumentNumber());
			enquiryKey.setTotal(iKeyValuePair.getTotal());
			enquiryKey.setStatus(iKeyValuePair.getStatus());
			enquiryKey.setNotes(iKeyValuePair.getNotes());
			enquiryKey.setDocumentDate(iKeyValuePair.getDocumentDate());
			enquiryKey.setDocumentType("Inquiry");
			enquiryList.add(enquiryKey);
		}
		dropdown.setInquiry(enquiryList);

		return dropdown;
	}


}
