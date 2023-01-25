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
import java.time.Year;
import java.time.YearMonth;
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
		if(workOrderStatus!=null) {
			if (workOrderStatus.getWorkOrderId() == null||workOrderStatus.getWorkOrderId().isEmpty()) {
				workOrderStatus.setWorkOrderId(null);
			}
			if (workOrderStatus.getWorkOrderSbu() == null||workOrderStatus.getWorkOrderSbu().isEmpty()) {
				workOrderStatus.setWorkOrderSbu(null);
			}
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
		if(efficiencyRecord!=null) {
			if (efficiencyRecord.getProcessedBy() == null||efficiencyRecord.getProcessedBy().isEmpty()) {
				efficiencyRecord.setProcessedBy(null);
			}
			if (efficiencyRecord.getJobCardType() == null||efficiencyRecord.getJobCardType().isEmpty()) {
				efficiencyRecord.setJobCardType(null);
			}
		}

		List<EfficiencyRecordImpl> data = reportRepository.getEfficiencyRecord(efficiencyRecord.getStartDate(), efficiencyRecord.getEndDate(),
				efficiencyRecord.getJobCardType(),
				efficiencyRecord.getProcessedBy());
		return data;
	}
	//--------------------------------------------Quotation Status------------------------------------------------------------------------
	public List<QuotationStatusImpl> getQuotationStatus(QuotationStatus quotationStatus) throws ParseException {

		if(quotationStatus!=null) {
			if (quotationStatus.getQuoteId() == null||quotationStatus.getQuoteId().isEmpty()) {
				quotationStatus.setQuoteId(null);
			}
			if (quotationStatus.getStatus() == null||quotationStatus.getStatus().isEmpty()) {
				quotationStatus.setStatus(null);
			}
			if (quotationStatus.getRequirementType() == null||quotationStatus.getRequirementType().isEmpty()) {
				quotationStatus.setRequirementType(null);
			}
			if (quotationStatus.getSbu() == null||quotationStatus.getSbu().isEmpty()) {
				quotationStatus.setSbu(null);
			}
		}

		List<QuotationStatusImpl> data = reportRepository.getQuotationStatus(quotationStatus.getQuoteId(),
				quotationStatus.getStatus(),
				quotationStatus.getRequirementType(),
				quotationStatus.getSbu());
		return data;
	}
	//--------------------------------------------EnquiryStatus------------------------------------------------------------------------
	public List<EnquiryStatusImpl> getEnquiryStatus(EnquiryStatus enquiryStatus) throws ParseException {

		if(enquiryStatus!=null) {
			if (enquiryStatus.getEnquiryId() == null || enquiryStatus.getEnquiryId().isEmpty()) {
				enquiryStatus.setEnquiryId(null);
			}
			if (enquiryStatus.getEnquiryId() == null || enquiryStatus.getEnquiryStatus().isEmpty()) {
				enquiryStatus.setEnquiryStatus(null);
			}
			if (enquiryStatus.getRequirementType() == null || enquiryStatus.getRequirementType().isEmpty()) {
				enquiryStatus.setRequirementType(null);
			}
			if (enquiryStatus.getSbu() == null || enquiryStatus.getSbu().isEmpty()) {
				enquiryStatus.setSbu(null);
			}
		}

		List<EnquiryStatusImpl> data = reportRepository.getEnquiryStatus(enquiryStatus.getEnquiryId(),
				enquiryStatus.getEnquiryStatus(),
				enquiryStatus.getRequirementType(),
				enquiryStatus.getSbu());
		return data;
	}
	//--------------------------------------------Fillrate Status------------------------------------------------------------------------
	public List<FillrateStatusImpl> getFillrateStatus(FillrateStatus fillrateStatus) throws ParseException {

		if(fillrateStatus!=null){
			if(fillrateStatus.getPhase()==null||fillrateStatus.getPhase().isEmpty()){
				fillrateStatus.setPhase(null);
			}
			if(fillrateStatus.getStoreNumber()==null||fillrateStatus.getStoreNumber().isEmpty()){
				fillrateStatus.setStoreNumber(null);
			}
			if(fillrateStatus.getStorageType()==null||fillrateStatus.getStorageType().isEmpty()){
				fillrateStatus.setStorageType(null);
			}
			if(fillrateStatus.getStatus()==null||fillrateStatus.getStatus().isEmpty()){
				fillrateStatus.setStatus(null);
			}
		}

		List<FillrateStatusImpl> data = reportRepository.getFillrateStatus(fillrateStatus.getPhase(),
				fillrateStatus.getStoreNumber(),
				fillrateStatus.getStorageType(),
				fillrateStatus.getStatus());
		return data;
	}

	//--------------------------------------------Contract Renewal Status------------------------------------------------------------------------
	public List<ContractRenewalStatusImpl> getContractRenewalStatus(ContractRenewalStatus contractRenewalStatus) throws ParseException {

		if(contractRenewalStatus!=null){
			if(contractRenewalStatus.getPhase()==null||contractRenewalStatus.getPhase().isEmpty()){
				contractRenewalStatus.setPhase(null);
			}
			if(contractRenewalStatus.getPhase()==null||contractRenewalStatus.getStoreNumber().isEmpty()){
				contractRenewalStatus.setPhase(null);
			}
			if(contractRenewalStatus.getStorageType()==null||contractRenewalStatus.getStorageType().isEmpty()){
				contractRenewalStatus.setStorageType(null);
			}
		}

		List<ContractRenewalStatusImpl> data = reportRepository.getContractRenewalStatus(contractRenewalStatus.getPhase(),
				contractRenewalStatus.getStoreNumber(),
				contractRenewalStatus.getStorageType());
		return data;
	}

	//--------------------------------------------Payment Due Status------------------------------------------------------------------------
	public List<PaymentDueStatusReportImpl> getPaymentDueStatus(PaymentDueStatus paymentDueStatus) throws ParseException {

		if(paymentDueStatus!=null) {
			if (paymentDueStatus.getAgreementNumber() == null||paymentDueStatus.getAgreementNumber().isEmpty()) {
				paymentDueStatus.setAgreementNumber(null);
			}
			if (paymentDueStatus.getCustomerName() == null||paymentDueStatus.getCustomerName().isEmpty()) {
				paymentDueStatus.setCustomerName(null);
			}
			if (paymentDueStatus.getCustomerCode() == null||paymentDueStatus.getCustomerCode().isEmpty()) {
				paymentDueStatus.setCustomerCode(null);
			}
			if (paymentDueStatus.getPhoneNumber() == null||paymentDueStatus.getPhoneNumber().isEmpty()) {
				paymentDueStatus.setPhoneNumber(null);
			}
			if (paymentDueStatus.getSecondaryNumber() == null||paymentDueStatus.getSecondaryNumber().isEmpty()) {
				paymentDueStatus.setSecondaryNumber(null);
			}
			if (paymentDueStatus.getCivilId() == null||paymentDueStatus.getCivilId().isEmpty()) {
				paymentDueStatus.setCivilId(null);
			}
		}
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

		if(documentStatus!=null) {
			if (documentStatusInput.getCustomerCode() == null || documentStatusInput.getCustomerCode().isEmpty()) {
				documentStatusInput.setCustomerCode(null);
			}
			if (documentStatusInput.getDocumentType() == null || documentStatusInput.getDocumentType().isEmpty()) {
				documentStatusInput.setDocumentType(null);
			}
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
			documentStatusKey.setDocumentType("Inquiry");

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
				if(documentType.equalsIgnoreCase("inquiry")){
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

	public BilledPaid getBilledPaid (Year year) throws Exception {

		BilledPaid billedPaid = new BilledPaid();
	//ustorage billed--------------------------------------------------------------------------------
		InvoiceAmount invoiceAmount = new InvoiceAmount();
		invoiceAmount.setBilled(new ArrayList<>());
		// January Month
		Date[] dates1 = DateUtils.getFirstMonth(year);
		List<Float> firstMonth = reportRepository.getUstorageInvoiceAmount( dates1[0], dates1[1]);
		invoiceAmount.getBilled().add(firstMonth.get(0));
		// February Month
		Date[] dates2 = DateUtils.getSecondMonth(year);
		List<Float> secondMonth = reportRepository.getUstorageInvoiceAmount( dates2[0], dates2[1]);
		invoiceAmount.getBilled().add(secondMonth.get(0));
		// March Month
		Date[] dates3 = DateUtils.getThirdMonth(year);
		List<Float> thirdMonth = reportRepository.getUstorageInvoiceAmount( dates3[0], dates3[1]);
		invoiceAmount.getBilled().add(thirdMonth.get(0));
		// April Month
		Date[] dates4 = DateUtils.getFourthMonth(year);
		List<Float> fourthMonth = reportRepository.getUstorageInvoiceAmount( dates4[0], dates4[1]);
		invoiceAmount.getBilled().add(fourthMonth.get(0));
		// May Month
		Date[] dates5 = DateUtils.getFifthMonth(year);
		List<Float> fifthMonth = reportRepository.getUstorageInvoiceAmount( dates5[0], dates5[1]);
		invoiceAmount.getBilled().add(fifthMonth.get(0));
		// June Month
		Date[] dates6 = DateUtils.getSixthMonth(year);
		List<Float> sixthMonth = reportRepository.getUstorageInvoiceAmount( dates6[0], dates6[1]);
		invoiceAmount.getBilled().add(sixthMonth.get(0));
		// July Month
		Date[] dates7 = DateUtils.getSeventhMonth(year);
		List<Float> seventhMonth = reportRepository.getUstorageInvoiceAmount( dates7[0], dates7[1]);
		invoiceAmount.getBilled().add(seventhMonth.get(0));
		// August Month
		Date[] dates8 = DateUtils.getEighthMonth(year);
		List<Float> eighthMonth = reportRepository.getUstorageInvoiceAmount( dates8[0], dates8[1]);
		invoiceAmount.getBilled().add(eighthMonth.get(0));
		// September Month
		Date[] dates9 = DateUtils.getNinthMonth(year);
		List<Float> ninthMonth = reportRepository.getUstorageInvoiceAmount( dates9[0], dates9[1]);
		invoiceAmount.getBilled().add(ninthMonth.get(0));
		// October Month
		Date[] dates10 = DateUtils.getTenthMonth(year);
		List<Float> tenthMonth = reportRepository.getUstorageInvoiceAmount( dates10[0], dates10[1]);
		invoiceAmount.getBilled().add(tenthMonth.get(0));
		// November Month
		Date[] dates11 = DateUtils.getEleventhMonth(year);
		List<Float> eleventhMonth = reportRepository.getUstorageInvoiceAmount( dates11[0], dates11[1]);
		invoiceAmount.getBilled().add(eleventhMonth.get(0));
		// December Month
		Date[] dates12 = DateUtils.getTwelfthMonth(year);
		List<Float> twelfthMonth = reportRepository.getUstorageInvoiceAmount( dates12[0], dates12[1]);
		invoiceAmount.getBilled().add(twelfthMonth.get(0));

		billedPaid.setUstorageBilled(invoiceAmount.getBilled());

//dashboard ulogistics billed-----------------------------------------------------------------------------
		UlogisticsInvoiceAmount ulogisticsInvoiceAmount = new UlogisticsInvoiceAmount();
		ulogisticsInvoiceAmount.setBilled(new ArrayList<>());
		// January Month
		Date[] uldates = DateUtils.getFirstMonth(year);
		List<Float> ulfirstMonth = reportRepository.getUlogisticsInvoiceAmount( uldates[0], uldates[1]);
		ulogisticsInvoiceAmount.getBilled().add(ulfirstMonth.get(0));
		// February Month
		Date[] uldates1 = DateUtils.getSecondMonth(year);
		List<Float> ulsecondMonth = reportRepository.getUlogisticsInvoiceAmount( uldates1[0], uldates1[1]);
		ulogisticsInvoiceAmount.getBilled().add(ulsecondMonth.get(0));
		// March Month
		Date[] uldates2 = DateUtils.getThirdMonth(year);
		List<Float> ulthirdMonth = reportRepository.getUlogisticsInvoiceAmount( uldates2[0], uldates2[1]);
		ulogisticsInvoiceAmount.getBilled().add(ulthirdMonth.get(0));
		// April Month
		Date[] uldates3 = DateUtils.getFourthMonth(year);
		List<Float> ulfourthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates3[0], uldates3[1]);
		ulogisticsInvoiceAmount.getBilled().add(ulfourthMonth.get(0));
		// May Month
		Date[] uldates4 = DateUtils.getFifthMonth(year);
		List<Float> ulfifthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates4[0], uldates4[1]);
		ulogisticsInvoiceAmount.getBilled().add(ulfifthMonth.get(0));
		// June Month
		Date[] uldates5 = DateUtils.getSixthMonth(year);
		List<Float> ulsixthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates5[0], uldates5[1]);
		ulogisticsInvoiceAmount.getBilled().add(ulsixthMonth.get(0));
		// July Month
		Date[] uldates6 = DateUtils.getSeventhMonth(year);
		List<Float> ulseventhMonth = reportRepository.getUlogisticsInvoiceAmount( uldates6[0], uldates6[1]);
		ulogisticsInvoiceAmount.getBilled().add(ulseventhMonth.get(0));
		// August Month
		Date[] uldates7 = DateUtils.getEighthMonth(year);
		List<Float> uleighthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates7[0], uldates7[1]);
		ulogisticsInvoiceAmount.getBilled().add(uleighthMonth.get(0));
		// September Month
		Date[] uldates8 = DateUtils.getNinthMonth(year);
		List<Float> ulninthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates8[0], uldates8[1]);
		ulogisticsInvoiceAmount.getBilled().add(ulninthMonth.get(0));
		// October Month
		Date[] uldates9 = DateUtils.getTenthMonth(year);
		List<Float> ultenthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates9[0], uldates9[1]);
		ulogisticsInvoiceAmount.getBilled().add(ultenthMonth.get(0));
		// November Month
		Date[] uldates10 = DateUtils.getEleventhMonth(year);
		List<Float> uleleventhMonth = reportRepository.getUlogisticsInvoiceAmount( uldates10[0], uldates10[1]);
		ulogisticsInvoiceAmount.getBilled().add(uleleventhMonth.get(0));
		// December Month
		Date[] uldates11 = DateUtils.getTwelfthMonth(year);
		List<Float> ultwelfthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates11[0], uldates11[1]);
		ulogisticsInvoiceAmount.getBilled().add(ultwelfthMonth.get(0));

		billedPaid.setUlogisticsBilled(ulogisticsInvoiceAmount.getBilled());

//dashboard ustorage paid-------------------------------------------------------------------------
		PaidAmount paidAmount = new PaidAmount();
		paidAmount.setPaid(new ArrayList<>());
		// January Month
		Date[] sdates = DateUtils.getFirstMonth(year);
		List<Float> usfirstMonth = reportRepository.getUstoragePaidAmount( sdates[0], sdates[1]);
		paidAmount.getPaid().add(usfirstMonth.get(0));
		// February Month
		Date[] sdates1 = DateUtils.getSecondMonth(year);
		List<Float> ussecondMonth = reportRepository.getUstoragePaidAmount( sdates1[0], sdates1[1]);
		paidAmount.getPaid().add(ussecondMonth.get(0));
		// March Month
		Date[] sdates2 = DateUtils.getThirdMonth(year);
		List<Float> usthirdMonth = reportRepository.getUstoragePaidAmount( sdates2[0], sdates2[1]);
		paidAmount.getPaid().add(usthirdMonth.get(0));
		// April Month
		Date[] sdates3 = DateUtils.getFourthMonth(year);
		List<Float> usfourthMonth = reportRepository.getUstoragePaidAmount( sdates3[0], sdates3[1]);
		paidAmount.getPaid().add(usfourthMonth.get(0));
		// May Month
		Date[] sdates4 = DateUtils.getFifthMonth(year);
		List<Float> usfifthMonth = reportRepository.getUstoragePaidAmount( sdates4[0], sdates4[1]);
		paidAmount.getPaid().add(usfifthMonth.get(0));
		// June Month
		Date[] sdates5 = DateUtils.getSixthMonth(year);
		List<Float> ussixthMonth = reportRepository.getUstoragePaidAmount( sdates5[0], sdates5[1]);
		paidAmount.getPaid().add(ussixthMonth.get(0));
		// July Month
		Date[] sdates6 = DateUtils.getSeventhMonth(year);
		List<Float> usseventhMonth = reportRepository.getUstoragePaidAmount( sdates6[0], sdates6[1]);
		paidAmount.getPaid().add(usseventhMonth.get(0));
		// August Month
		Date[] sdates7 = DateUtils.getEighthMonth(year);
		List<Float> useighthMonth = reportRepository.getUstoragePaidAmount( sdates7[0], sdates7[1]);
		paidAmount.getPaid().add(useighthMonth.get(0));
		// September Month
		Date[] sdates8 = DateUtils.getNinthMonth(year);
		List<Float> usninthMonth = reportRepository.getUstoragePaidAmount( sdates8[0], sdates8[1]);
		paidAmount.getPaid().add(usninthMonth.get(0));
		// October Month
		Date[] sdates9 = DateUtils.getTenthMonth(year);
		List<Float> ustenthMonth = reportRepository.getUstoragePaidAmount( sdates9[0], sdates9[1]);
		paidAmount.getPaid().add(ustenthMonth.get(0));
		// November Month
		Date[] sdates10 = DateUtils.getEleventhMonth(year);
		List<Float> useleventhMonth = reportRepository.getUstoragePaidAmount( sdates10[0], sdates10[1]);
		paidAmount.getPaid().add(useleventhMonth.get(0));
		// December Month
		Date[] sdates11 = DateUtils.getTwelfthMonth(year);
		List<Float> ustwelfthMonth = reportRepository.getUstoragePaidAmount( sdates11[0], sdates11[1]);
		paidAmount.getPaid().add(ustwelfthMonth.get(0));

		billedPaid.setUstoragePaid(paidAmount.getPaid());

//dashboard ulogistics paid-----------------------------------------------------------------------------
		UlogisticsPaidAmount ulogisticsPaidAmount = new UlogisticsPaidAmount();
		ulogisticsPaidAmount.setPaid(new ArrayList<>());
		// January Month
		Date[] ldates = DateUtils.getFirstMonth(year);
		List<Float> lfirstMonth = reportRepository.getUlogisticsPaidAmount( ldates[0], ldates[1]);
		ulogisticsPaidAmount.getPaid().add(lfirstMonth.get(0));
		// February Month
		Date[] ldates1 = DateUtils.getSecondMonth(year);
		List<Float> lsecondMonth = reportRepository.getUlogisticsPaidAmount( ldates1[0], ldates1[1]);
		ulogisticsPaidAmount.getPaid().add(lsecondMonth.get(0));
		// March Month
		Date[] ldates2 = DateUtils.getThirdMonth(year);
		List<Float> lthirdMonth = reportRepository.getUlogisticsPaidAmount( ldates2[0], ldates2[1]);
		ulogisticsPaidAmount.getPaid().add(lthirdMonth.get(0));
		// April Month
		Date[] ldates3 = DateUtils.getFourthMonth(year);
		List<Float> lfourthMonth = reportRepository.getUlogisticsPaidAmount( ldates3[0], ldates3[1]);
		ulogisticsPaidAmount.getPaid().add(lfourthMonth.get(0));
		// May Month
		Date[] ldates4 = DateUtils.getFifthMonth(year);
		List<Float> lfifthMonth = reportRepository.getUlogisticsPaidAmount( ldates4[0], ldates4[1]);
		ulogisticsPaidAmount.getPaid().add(lfifthMonth.get(0));
		// June Month
		Date[] ldates5 = DateUtils.getSixthMonth(year);
		List<Float> lsixthMonth = reportRepository.getUlogisticsPaidAmount( ldates5[0], ldates5[1]);
		ulogisticsPaidAmount.getPaid().add(lsixthMonth.get(0));
		// July Month
		Date[] ldates6 = DateUtils.getSeventhMonth(year);
		List<Float> lseventhMonth = reportRepository.getUlogisticsPaidAmount( ldates6[0], ldates6[1]);
		ulogisticsPaidAmount.getPaid().add(lseventhMonth.get(0));
		// August Month
		Date[] ldates7 = DateUtils.getEighthMonth(year);
		List<Float> leighthMonth = reportRepository.getUlogisticsPaidAmount( ldates7[0], ldates7[1]);
		ulogisticsPaidAmount.getPaid().add(leighthMonth.get(0));
		// September Month
		Date[] ldates8 = DateUtils.getNinthMonth(year);
		List<Float> lninthMonth = reportRepository.getUlogisticsPaidAmount( ldates8[0], ldates8[1]);
		ulogisticsPaidAmount.getPaid().add(lninthMonth.get(0));
		// October Month
		Date[] ldates9 = DateUtils.getTenthMonth(year);
		List<Float> ltenthMonth = reportRepository.getUlogisticsPaidAmount( ldates9[0], ldates9[1]);
		ulogisticsPaidAmount.getPaid().add(ltenthMonth.get(0));
		// November Month
		Date[] ldates10 = DateUtils.getEleventhMonth(year);
		List<Float> leleventhMonth = reportRepository.getUlogisticsPaidAmount( ldates10[0], ldates10[1]);
		ulogisticsPaidAmount.getPaid().add(leleventhMonth.get(0));
		// December Month
		Date[] ldates11 = DateUtils.getTwelfthMonth(year);
		List<Float> ltwelfthMonth = reportRepository.getUlogisticsPaidAmount( ldates11[0], ldates11[1]);
		ulogisticsPaidAmount.getPaid().add(ltwelfthMonth.get(0));

		billedPaid.setUlogisticsPaid(ulogisticsPaidAmount.getPaid());

		return billedPaid;
	}

	public LeadAndCustomer getLeadAndCustomer (Year year) throws ParseException {

		LeadAndCustomer leadAndCustomer = new LeadAndCustomer();
		//Lead Count--------------------------------------------------------------------------------
		LeadCustomerCount leadCount = new LeadCustomerCount();
		leadCount.setCount(new ArrayList<>());
		// January Month
		Date[] dates = DateUtils.getFirstMonth(year);
		List<Integer> firstMonth = reportRepository.getLeadCount(dates[0], dates[1]);
		leadCount.getCount().add(firstMonth.get(0));
		// February Month
		Date[] dates1 = DateUtils.getSecondMonth(year);
		List<Integer> secondMonth = reportRepository.getLeadCount(dates1[0], dates1[1]);
		leadCount.getCount().add(secondMonth.get(0));
		// March Month
		Date[] dates2 = DateUtils.getThirdMonth(year);
		List<Integer> thirdMonth = reportRepository.getLeadCount(dates2[0], dates2[1]);
		leadCount.getCount().add(thirdMonth.get(0));
		// April Month
		Date[] dates3 = DateUtils.getFourthMonth(year);
		List<Integer> fourthMonth = reportRepository.getLeadCount(dates3[0], dates3[1]);
		leadCount.getCount().add(fourthMonth.get(0));
		// May Month
		Date[] dates4 = DateUtils.getFifthMonth(year);
		List<Integer> fifthMonth = reportRepository.getLeadCount(dates4[0], dates4[1]);
		leadCount.getCount().add(fifthMonth.get(0));
		// June Month
		Date[] dates5 = DateUtils.getSixthMonth(year);
		List<Integer> sixthMonth = reportRepository.getLeadCount(dates5[0], dates5[1]);
		leadCount.getCount().add(sixthMonth.get(0));
		// July Month
		Date[] dates6 = DateUtils.getSeventhMonth(year);
		List<Integer> seventhMonth = reportRepository.getLeadCount( dates6[0], dates6[1]);
		leadCount.getCount().add(seventhMonth.get(0));
		// August Month
		Date[] dates7 = DateUtils.getEighthMonth(year);
		List<Integer> eighthMonth = reportRepository.getLeadCount( dates7[0], dates7[1]);
		leadCount.getCount().add(eighthMonth.get(0));
		// September Month
		Date[] dates8 = DateUtils.getNinthMonth(year);
		List<Integer> ninthMonth = reportRepository.getLeadCount( dates8[0], dates8[1]);
		leadCount.getCount().add(ninthMonth.get(0));
		// October Month
		Date[] dates9 = DateUtils.getTenthMonth(year);
		List<Integer> tenthMonth = reportRepository.getLeadCount( dates9[0], dates9[1]);
		leadCount.getCount().add(tenthMonth.get(0));
		// November Month
		Date[] dates10 = DateUtils.getEleventhMonth(year);
		List<Integer> eleventhMonth = reportRepository.getLeadCount( dates10[0], dates10[1]);
		leadCount.getCount().add(eleventhMonth.get(0));
		// December Month
		Date[] dates11 = DateUtils.getTwelfthMonth(year);
		List<Integer> twelfthMonth = reportRepository.getLeadCount( dates11[0], dates11[1]);
		leadCount.getCount().add(twelfthMonth.get(0));

		leadAndCustomer.setLead(leadCount.getCount());

		//Customer Count--------------------------------------------------------------------------------
		LeadCustomerCount customerCount = new LeadCustomerCount();
		customerCount.setCount(new ArrayList<>());
		// January Month
		Date[] cdates = DateUtils.getFirstMonth(year);
		List<Integer> cFirstMonth = reportRepository.getCustomerCount(cdates[0], cdates[1]);
		customerCount.getCount().add(cFirstMonth.get(0));
		// February Month
		Date[] cdates1 = DateUtils.getSecondMonth(year);
		List<Integer> cSecondMonth = reportRepository.getCustomerCount(cdates1[0], cdates1[1]);
		customerCount.getCount().add(cSecondMonth.get(0));
		// March Month
		Date[] cdates2 = DateUtils.getThirdMonth(year);
		List<Integer> cThirdMonth = reportRepository.getCustomerCount(cdates2[0], cdates2[1]);
		customerCount.getCount().add(cThirdMonth.get(0));
		// April Month
		Date[] cdates3 = DateUtils.getFourthMonth(year);
		List<Integer> cFourthMonth = reportRepository.getCustomerCount(cdates3[0], cdates3[1]);
		customerCount.getCount().add(cFourthMonth.get(0));
		// May Month
		Date[] cdates4 = DateUtils.getFifthMonth(year);
		List<Integer> cFifthMonth = reportRepository.getCustomerCount(cdates4[0], cdates4[1]);
		customerCount.getCount().add(cFifthMonth.get(0));
		// June Month
		Date[] cdates5 = DateUtils.getSixthMonth(year);
		List<Integer> cSixthMonth = reportRepository.getCustomerCount(cdates5[0], cdates5[1]);
		customerCount.getCount().add(cSixthMonth.get(0));
		// July Month
		Date[] cdates6 = DateUtils.getSeventhMonth(year);
		List<Integer> cSeventhMonth = reportRepository.getCustomerCount( cdates6[0], cdates6[1]);
		customerCount.getCount().add(cSeventhMonth.get(0));
		// August Month
		Date[] cdates7 = DateUtils.getEighthMonth(year);
		List<Integer> cEighthMonth = reportRepository.getCustomerCount( cdates7[0], cdates7[1]);
		customerCount.getCount().add(cEighthMonth.get(0));
		// September Month
		Date[] cdates8 = DateUtils.getNinthMonth(year);
		List<Integer> cNinthMonth = reportRepository.getCustomerCount( cdates8[0], cdates8[1]);
		customerCount.getCount().add(cNinthMonth.get(0));
		// October Month
		Date[] cdates9 = DateUtils.getTenthMonth(year);
		List<Integer> cTenthMonth = reportRepository.getCustomerCount( cdates9[0], cdates9[1]);
		customerCount.getCount().add(cTenthMonth.get(0));
		// November Month
		Date[] cdates10 = DateUtils.getEleventhMonth(year);
		List<Integer> cEleventhMonth = reportRepository.getCustomerCount( cdates10[0], cdates10[1]);
		customerCount.getCount().add(cEleventhMonth.get(0));
		// December Month
		Date[] cdates11 = DateUtils.getTwelfthMonth(year);
		List<Integer> cTwelfthMonth = reportRepository.getCustomerCount( cdates11[0], cdates11[1]);
		customerCount.getCount().add(cTwelfthMonth.get(0));

		leadAndCustomer.setCustomer(customerCount.getCount());

		//Lead Count-Ulogistics-------------------------------------------------------------------------------
		LeadCustomerCount uleadCount = new LeadCustomerCount();
		uleadCount.setCount(new ArrayList<>());
		// January Month
		Date[] ldates = DateUtils.getFirstMonth(year);
		List<Integer> lFirstMonth = reportRepository.getULeadCount(ldates[0], ldates[1]);
		uleadCount.getCount().add(lFirstMonth.get(0));
		// February Month
		Date[] ldates1 = DateUtils.getSecondMonth(year);
		List<Integer> lSecondMonth = reportRepository.getULeadCount(ldates1[0], ldates1[1]);
		uleadCount.getCount().add(lSecondMonth.get(0));
		// March Month
		Date[] ldates2 = DateUtils.getThirdMonth(year);
		List<Integer> lThirdMonth = reportRepository.getULeadCount(ldates2[0], ldates2[1]);
		uleadCount.getCount().add(lThirdMonth.get(0));
		// April Month
		Date[] ldates3 = DateUtils.getFourthMonth(year);
		List<Integer> lFourthMonth = reportRepository.getULeadCount(ldates3[0], ldates3[1]);
		uleadCount.getCount().add(lFourthMonth.get(0));
		// May Month
		Date[] ldates4 = DateUtils.getFifthMonth(year);
		List<Integer> lFifthMonth = reportRepository.getULeadCount(ldates4[0], ldates4[1]);
		uleadCount.getCount().add(lFifthMonth.get(0));
		// June Month
		Date[] ldates5 = DateUtils.getSixthMonth(year);
		List<Integer> lSixthMonth = reportRepository.getULeadCount(ldates5[0], ldates5[1]);
		uleadCount.getCount().add(lSixthMonth.get(0));
		// July Month
		Date[] ldates6 = DateUtils.getSeventhMonth(year);
		List<Integer> lSeventhMonth = reportRepository.getULeadCount( ldates6[0], ldates6[1]);
		uleadCount.getCount().add(lSeventhMonth.get(0));
		// August Month
		Date[] ldates7 = DateUtils.getEighthMonth(year);
		List<Integer> lEighthMonth = reportRepository.getULeadCount( ldates7[0], ldates7[1]);
		uleadCount.getCount().add(lEighthMonth.get(0));
		// September Month
		Date[] ldates8 = DateUtils.getNinthMonth(year);
		List<Integer> lNinthMonth = reportRepository.getULeadCount( ldates8[0], ldates8[1]);
		uleadCount.getCount().add(lNinthMonth.get(0));
		// October Month
		Date[] ldates9 = DateUtils.getTenthMonth(year);
		List<Integer> lTenthMonth = reportRepository.getULeadCount( ldates9[0], ldates9[1]);
		uleadCount.getCount().add(lTenthMonth.get(0));
		// November Month
		Date[] ldates10 = DateUtils.getEleventhMonth(year);
		List<Integer> lEleventhMonth = reportRepository.getULeadCount( ldates10[0], ldates10[1]);
		uleadCount.getCount().add(lEleventhMonth.get(0));
		// December Month
		Date[] ldates11 = DateUtils.getTwelfthMonth(year);
		List<Integer> lTwelfthMonth = reportRepository.getULeadCount( ldates11[0], ldates11[1]);
		uleadCount.getCount().add(lTwelfthMonth.get(0));

		leadAndCustomer.setUllead(uleadCount.getCount());

		//Customer Count-Ulogistics-------------------------------------------------------------------------------
		LeadCustomerCount ucustomerCount = new LeadCustomerCount();
		ucustomerCount.setCount(new ArrayList<>());
		// January Month
		Date[] lcdates = DateUtils.getFirstMonth(year);
		List<Integer> lcFirstMonth = reportRepository.getUCustomerCount(lcdates[0], lcdates[1]);
		ucustomerCount.getCount().add(lcFirstMonth.get(0));
		// February Month
		Date[] lcdates1 = DateUtils.getSecondMonth(year);
		List<Integer> lcSecondMonth = reportRepository.getUCustomerCount(lcdates1[0], lcdates1[1]);
		ucustomerCount.getCount().add(lcSecondMonth.get(0));
		// March Month
		Date[] lcdates2 = DateUtils.getThirdMonth(year);
		List<Integer> lcThirdMonth = reportRepository.getUCustomerCount(lcdates2[0], lcdates2[1]);
		ucustomerCount.getCount().add(lcThirdMonth.get(0));
		// April Month
		Date[] lcdates3 = DateUtils.getFourthMonth(year);
		List<Integer> lcFourthMonth = reportRepository.getUCustomerCount(lcdates3[0], lcdates3[1]);
		ucustomerCount.getCount().add(lcFourthMonth.get(0));
		// May Month
		Date[] lcdates4 = DateUtils.getFifthMonth(year);
		List<Integer> lcFifthMonth = reportRepository.getUCustomerCount(lcdates4[0], lcdates4[1]);
		ucustomerCount.getCount().add(lcFifthMonth.get(0));
		// June Month
		Date[] lcdates5 = DateUtils.getSixthMonth(year);
		List<Integer> lcSixthMonth = reportRepository.getUCustomerCount(lcdates5[0], lcdates5[1]);
		ucustomerCount.getCount().add(lcSixthMonth.get(0));
		// July Month
		Date[] lcdates6 = DateUtils.getSeventhMonth(year);
		List<Integer> lcSeventhMonth = reportRepository.getUCustomerCount( lcdates6[0], lcdates6[1]);
		ucustomerCount.getCount().add(lcSeventhMonth.get(0));
		// August Month
		Date[] lcdates7 = DateUtils.getEighthMonth(year);
		List<Integer> lcEighthMonth = reportRepository.getUCustomerCount( lcdates7[0], lcdates7[1]);
		ucustomerCount.getCount().add(lcEighthMonth.get(0));
		// September Month
		Date[] lcdates8 = DateUtils.getNinthMonth(year);
		List<Integer> lcNinthMonth = reportRepository.getUCustomerCount( lcdates8[0], lcdates8[1]);
		ucustomerCount.getCount().add(lcNinthMonth.get(0));
		// October Month
		Date[] lcdates9 = DateUtils.getTenthMonth(year);
		List<Integer> lcTenthMonth = reportRepository.getUCustomerCount( lcdates9[0], lcdates9[1]);
		ucustomerCount.getCount().add(lcTenthMonth.get(0));
		// November Month
		Date[] lcdates10 = DateUtils.getEleventhMonth(year);
		List<Integer> lcEleventhMonth = reportRepository.getUCustomerCount( lcdates10[0], lcdates10[1]);
		ucustomerCount.getCount().add(lcEleventhMonth.get(0));
		// December Month
		Date[] lcdates11 = DateUtils.getTwelfthMonth(year);
		List<Integer> lcTwelfthMonth = reportRepository.getUCustomerCount( lcdates11[0], lcdates11[1]);
		ucustomerCount.getCount().add(lcTwelfthMonth.get(0));

		leadAndCustomer.setUlCustomer(ucustomerCount.getCount());

		return leadAndCustomer;
	}

}
