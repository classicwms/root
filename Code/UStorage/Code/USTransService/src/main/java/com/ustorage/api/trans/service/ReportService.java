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

	public BilledPaid getBilledPaid () throws ParseException {

		BilledPaid billedPaid = new BilledPaid();
	//ustorage billed--------------------------------------------------------------------------------
		InvoiceAmount invoiceAmount = new InvoiceAmount();
		invoiceAmount.setBilled(new ArrayList<>());
		// Current Month
		Date[] dates = DateUtils.getCurrentMonth();
		List<Float> currentMonth = reportRepository.getUstorageInvoiceAmount( dates[0], dates[1]);
		if(currentMonth!=null){
			invoiceAmount.getBilled().add(currentMonth.get(0));
		}
		// Previous One Month
		Date[] dates1 = DateUtils.getPreviousOneMonth();
		List<Float> previousOneMonth = reportRepository.getUstorageInvoiceAmount( dates1[0], dates1[1]);
		if(previousOneMonth!=null){
			invoiceAmount.getBilled().add(previousOneMonth.get(0));
		}
		// Previous Second Month
		Date[] dates2 = DateUtils.getPreviousSecondMonth();
		List<Float> previousSecondMonth = reportRepository.getUstorageInvoiceAmount( dates2[0], dates2[1]);
		if(previousSecondMonth!=null){
			invoiceAmount.getBilled().add(previousSecondMonth.get(0));
		}
		// Previous Third Month
		Date[] dates3 = DateUtils.getPreviousThirdMonth();
		List<Float> previousThirdMonth = reportRepository.getUstorageInvoiceAmount( dates3[0], dates3[1]);
		if(previousThirdMonth!=null){
			invoiceAmount.getBilled().add(previousThirdMonth.get(0));
		}
		// Previous Fourth Month
		Date[] dates4 = DateUtils.getPreviousFourthMonth();
		List<Float> previousFourthMonth = reportRepository.getUstorageInvoiceAmount( dates4[0], dates4[1]);
		if(previousFourthMonth!=null){
			invoiceAmount.getBilled().add(previousFourthMonth.get(0));
		}
		// Previous Fifth Month
		Date[] dates5 = DateUtils.getPreviousFifthMonth();
		List<Float> previousFifthMonth = reportRepository.getUstorageInvoiceAmount( dates5[0], dates5[1]);
		if(previousFifthMonth!=null){
			invoiceAmount.getBilled().add(previousFifthMonth.get(0));
		}
		// Previous Sixth Month
		Date[] dates6 = DateUtils.getPreviousSixthMonth();
		List<Float> previousSixthMonth = reportRepository.getUstorageInvoiceAmount( dates6[0], dates6[1]);
		if(previousSixthMonth!=null){
			invoiceAmount.getBilled().add(previousSixthMonth.get(0));
		}
		// Previous Seventh Month
		Date[] dates7 = DateUtils.getPreviousSeventhMonth();
		List<Float> previousSeventhMonth = reportRepository.getUstorageInvoiceAmount( dates7[0], dates7[1]);
		if(previousSeventhMonth!=null){
			invoiceAmount.getBilled().add(previousSeventhMonth.get(0));
		}
		// Previous Eighth Month
		Date[] dates8 = DateUtils.getPreviousEighthMonth();
		List<Float> previousEighthMonth = reportRepository.getUstorageInvoiceAmount( dates8[0], dates8[1]);
		if(previousEighthMonth!=null){
			invoiceAmount.getBilled().add(previousEighthMonth.get(0));
		}else{
			invoiceAmount.getBilled().add(previousEighthMonth.set(0,(float) 0));
		}
		// Previous Nineth Month
		Date[] dates9 = DateUtils.getPreviousNinethMonth();
		List<Float> previousNinethMonth = reportRepository.getUstorageInvoiceAmount( dates9[0], dates9[1]);
		if(previousNinethMonth!=null){
			invoiceAmount.getBilled().add(previousNinethMonth.get(0));
		}
		// Previous Tenth Month
		Date[] dates10 = DateUtils.getPreviousTenthMonth();
		List<Float> previousTenthMonth = reportRepository.getUstorageInvoiceAmount( dates10[0], dates10[1]);
		if(previousTenthMonth!=null){
			invoiceAmount.getBilled().add(previousTenthMonth.get(0));
		}
		// Previous Eleventh Month
		Date[] dates11 = DateUtils.getPreviousEleventhMonth();
		List<Float> previousEleventhMonth = reportRepository.getUstorageInvoiceAmount( dates11[0], dates11[1]);
		if(previousEleventhMonth!=null){
			invoiceAmount.getBilled().add(previousEleventhMonth.get(0));
		}

		billedPaid.setUstorageBilled(invoiceAmount.getBilled());

//dashboard ulogistics billed-----------------------------------------------------------------------------
		UlogisticsInvoiceAmount ulogisticsInvoiceAmount = new UlogisticsInvoiceAmount();
		ulogisticsInvoiceAmount.setBilled(new ArrayList<>());
		// Current Month
		Date[] uldates = DateUtils.getCurrentMonth();
		List<Float> ucurrentMonth = reportRepository.getUlogisticsInvoiceAmount( uldates[0], uldates[1]);
		if(ucurrentMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(ucurrentMonth.get(0));
		}
		// Previous One Month
		Date[] uldates1 = DateUtils.getPreviousOneMonth();
		List<Float> upreviousOneMonth = reportRepository.getUlogisticsInvoiceAmount( uldates1[0], uldates1[1]);
		if(upreviousOneMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousOneMonth.get(0));
		}
		// Previous Second Month
		Date[] uldates2 = DateUtils.getPreviousSecondMonth();
		List<Float> upreviousSecondMonth = reportRepository.getUlogisticsInvoiceAmount( uldates2[0], uldates2[1]);
		if(upreviousSecondMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousSecondMonth.get(0));
		}
		// Previous Third Month
		Date[] uldates3 = DateUtils.getPreviousThirdMonth();
		List<Float> upreviousThirdMonth = reportRepository.getUlogisticsInvoiceAmount( uldates3[0], uldates3[1]);
		if(upreviousThirdMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousThirdMonth.get(0));
		}
		// Previous Fourth Month
		Date[] uldates4 = DateUtils.getPreviousFourthMonth();
		List<Float> upreviousFourthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates4[0], uldates4[1]);
		if(upreviousFourthMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousFourthMonth.get(0));
		}
		// Previous Fifth Month
		Date[] uldates5 = DateUtils.getPreviousFifthMonth();
		List<Float> upreviousFifthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates5[0], uldates5[1]);
		if(upreviousFifthMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousFifthMonth.get(0));
		}
		// Previous Sixth Month
		Date[] uldates6 = DateUtils.getPreviousSixthMonth();
		List<Float> upreviousSixthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates6[0], uldates6[1]);
		if(upreviousSixthMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousSixthMonth.get(0));
		}
		// Previous Seventh Month
		Date[] uldates7 = DateUtils.getPreviousSeventhMonth();
		List<Float> upreviousSeventhMonth = reportRepository.getUlogisticsInvoiceAmount( uldates7[0], uldates7[1]);
		if(upreviousSeventhMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousSeventhMonth.get(0));
		}
		// Previous Eighth Month
		Date[] uldates8 = DateUtils.getPreviousEighthMonth();
		List<Float> upreviousEighthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates8[0], uldates8[1]);
		if(upreviousEighthMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousEighthMonth.get(0));
		}
		// Previous Nineth Month
		Date[] uldates9 = DateUtils.getPreviousNinethMonth();
		List<Float> upreviousNinethMonth = reportRepository.getUlogisticsInvoiceAmount( uldates9[0], uldates9[1]);
		if(upreviousNinethMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousNinethMonth.get(0));
		}
		// Previous Tenth Month
		Date[] uldates10 = DateUtils.getPreviousTenthMonth();
		List<Float> upreviousTenthMonth = reportRepository.getUlogisticsInvoiceAmount( uldates10[0], uldates10[1]);
		if(upreviousTenthMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousTenthMonth.get(0));
		}
		// Previous Eleventh Month
		Date[] uldates11 = DateUtils.getPreviousEleventhMonth();
		List<Float> upreviousEleventhMonth = reportRepository.getUlogisticsInvoiceAmount( uldates11[0], uldates11[1]);
		if(upreviousEleventhMonth!=null){
			ulogisticsInvoiceAmount.getBilled().add(upreviousEleventhMonth.get(0));
		}

		billedPaid.setUlogisticsBilled(ulogisticsInvoiceAmount.getBilled());

//dashboard ustorage paid-------------------------------------------------------------------------
		PaidAmount paidAmount = new PaidAmount();
		paidAmount.setPaid(new ArrayList<>());
		// Current Month
		Date[] sdates = DateUtils.getCurrentMonth();
		List<Float> scurrentMonth = reportRepository.getUstoragePaidAmount( sdates[0], sdates[1]);
		if(scurrentMonth!=null){
			paidAmount.getPaid().add(scurrentMonth.get(0));
		}
		// Previous One Month
		Date[] sdates1 = DateUtils.getPreviousOneMonth();
		List<Float> spreviousOneMonth = reportRepository.getUstoragePaidAmount( sdates1[0], sdates1[1]);
		if(spreviousOneMonth!=null){
			paidAmount.getPaid().add(spreviousOneMonth.get(0));
		}
		// Previous Second Month
		Date[] sdates2 = DateUtils.getPreviousSecondMonth();
		List<Float> spreviousSecondMonth = reportRepository.getUstoragePaidAmount( sdates2[0], sdates2[1]);
		if(spreviousSecondMonth!=null){
			paidAmount.getPaid().add(spreviousSecondMonth.get(0));
		}
		// Previous Third Month
		Date[] sdates3 = DateUtils.getPreviousThirdMonth();
		List<Float> spreviousThirdMonth = reportRepository.getUstoragePaidAmount( sdates3[0], sdates3[1]);
		if(spreviousThirdMonth!=null){
			paidAmount.getPaid().add(spreviousThirdMonth.get(0));
		}
		// Previous Fourth Month
		Date[] sdates4 = DateUtils.getPreviousFourthMonth();
		List<Float> spreviousFourthMonth = reportRepository.getUstoragePaidAmount( sdates4[0], sdates4[1]);
		if(spreviousFourthMonth!=null){
			paidAmount.getPaid().add(spreviousFourthMonth.get(0));
		}
		// Previous Fifth Month
		Date[] sdates5 = DateUtils.getPreviousFifthMonth();
		List<Float> spreviousFifthMonth = reportRepository.getUstoragePaidAmount( sdates5[0], sdates5[1]);
		if(spreviousFifthMonth!=null){
			paidAmount.getPaid().add(spreviousFifthMonth.get(0));
		}
		// Previous Sixth Month
		Date[] sdates6 = DateUtils.getPreviousSixthMonth();
		List<Float> spreviousSixthMonth = reportRepository.getUstoragePaidAmount( sdates6[0], sdates6[1]);
		if(spreviousSixthMonth!=null){
			paidAmount.getPaid().add(spreviousSixthMonth.get(0));
		}
		// Previous Seventh Month
		Date[] sdates7 = DateUtils.getPreviousSeventhMonth();
		List<Float> spreviousSeventhMonth = reportRepository.getUstoragePaidAmount( sdates7[0], sdates7[1]);
		if(spreviousSeventhMonth!=null){
			paidAmount.getPaid().add(spreviousSeventhMonth.get(0));
		}
		// Previous Eighth Month
		Date[] sdates8 = DateUtils.getPreviousEighthMonth();
		List<Float> spreviousEighthMonth = reportRepository.getUstoragePaidAmount( sdates8[0], sdates8[1]);
		if(spreviousEighthMonth!=null){
			paidAmount.getPaid().add(spreviousEighthMonth.get(0));
		}
		// Previous Nineth Month
		Date[] sdates9 = DateUtils.getPreviousNinethMonth();
		List<Float> spreviousNinethMonth = reportRepository.getUstoragePaidAmount( sdates9[0], sdates9[1]);
		if(spreviousNinethMonth!=null){
			paidAmount.getPaid().add(spreviousNinethMonth.get(0));
		}
		// Previous Tenth Month
		Date[] sdates10 = DateUtils.getPreviousTenthMonth();
		List<Float> spreviousTenthMonth = reportRepository.getUstoragePaidAmount( sdates10[0], sdates10[1]);
		if(spreviousTenthMonth!=null){
			paidAmount.getPaid().add(spreviousTenthMonth.get(0));
		}
		// Previous Eleventh Month
		Date[] sdates11 = DateUtils.getPreviousEleventhMonth();
		List<Float> spreviousEleventhMonth = reportRepository.getUstoragePaidAmount( sdates11[0], sdates11[1]);
		if(spreviousEleventhMonth!=null){
			paidAmount.getPaid().add(spreviousEleventhMonth.get(0));
		}
		billedPaid.setUstoragePaid(paidAmount.getPaid());

//dashboard ulogistics paid-----------------------------------------------------------------------------
		UlogisticsPaidAmount ulogisticsPaidAmount = new UlogisticsPaidAmount();
		ulogisticsPaidAmount.setPaid(new ArrayList<>());
		// Current Month
		Date[] ldates = DateUtils.getCurrentMonth();
		List<Float> lcurrentMonth = reportRepository.getUlogisticsPaidAmount( ldates[0], ldates[1]);
		if(lcurrentMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lcurrentMonth.get(0));
		}
		// Previous One Month
		Date[] ldates1 = DateUtils.getPreviousOneMonth();
		List<Float> lpreviousOneMonth = reportRepository.getUlogisticsPaidAmount( ldates1[0], ldates1[1]);
		if(lpreviousOneMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousOneMonth.get(0));
		}
		// Previous Second Month
		Date[] ldates2 = DateUtils.getPreviousSecondMonth();
		List<Float> lpreviousSecondMonth = reportRepository.getUlogisticsPaidAmount( ldates2[0], ldates2[1]);
		if(lpreviousSecondMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousSecondMonth.get(0));
		}
		// Previous Third Month
		Date[] ldates3 = DateUtils.getPreviousThirdMonth();
		List<Float> lpreviousThirdMonth = reportRepository.getUlogisticsPaidAmount( ldates3[0], ldates3[1]);
		if(lpreviousThirdMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousThirdMonth.get(0));
		}
		// Previous Fourth Month
		Date[] ldates4 = DateUtils.getPreviousFourthMonth();
		List<Float> lpreviousFourthMonth = reportRepository.getUlogisticsPaidAmount( ldates4[0], ldates4[1]);
		if(lpreviousFourthMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousFourthMonth.get(0));
		}
		// Previous Fifth Month
		Date[] ldates5 = DateUtils.getPreviousFifthMonth();
		List<Float> lpreviousFifthMonth = reportRepository.getUlogisticsPaidAmount( ldates5[0], ldates5[1]);
		if(lpreviousFifthMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousFifthMonth.get(0));
		}
		// Previous Sixth Month
		Date[] ldates6 = DateUtils.getPreviousSixthMonth();
		List<Float> lpreviousSixthMonth = reportRepository.getUlogisticsPaidAmount( ldates6[0], ldates6[1]);
		if(lpreviousSixthMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousSixthMonth.get(0));
		}
		// Previous Seventh Month
		Date[] ldates7 = DateUtils.getPreviousSeventhMonth();
		List<Float> lpreviousSeventhMonth = reportRepository.getUlogisticsPaidAmount( ldates7[0], ldates7[1]);
		if(lpreviousSeventhMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousSeventhMonth.get(0));
		}
		// Previous Eighth Month
		Date[] ldates8 = DateUtils.getPreviousEighthMonth();
		List<Float> lpreviousEighthMonth = reportRepository.getUlogisticsPaidAmount( ldates8[0], ldates8[1]);
		if(lpreviousEighthMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousEighthMonth.get(0));
		}
		// Previous Nineth Month
		Date[] ldates9 = DateUtils.getPreviousNinethMonth();
		List<Float> lpreviousNinethMonth = reportRepository.getUlogisticsPaidAmount( ldates9[0], ldates9[1]);
		if(lpreviousNinethMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousNinethMonth.get(0));
		}
		// Previous Tenth Month
		Date[] ldates10 = DateUtils.getPreviousTenthMonth();
		List<Float> lpreviousTenthMonth = reportRepository.getUlogisticsPaidAmount( ldates10[0], ldates10[1]);
		if(lpreviousTenthMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousTenthMonth.get(0));
		}
		// Previous Eleventh Month
		Date[] ldates11 = DateUtils.getPreviousEleventhMonth();
		List<Float> lpreviousEleventhMonth = reportRepository.getUlogisticsPaidAmount( ldates11[0], ldates11[1]);
		if(lpreviousEleventhMonth!=null){
			ulogisticsPaidAmount.getPaid().add(lpreviousEleventhMonth.get(0));
		}

		billedPaid.setUlogisticsPaid(ulogisticsPaidAmount.getPaid());

		return billedPaid;
	}

	public LeadAndCustomer getLeadAndCustomer () throws ParseException {

		LeadAndCustomer leadAndCustomer = new LeadAndCustomer();
		//Lead Count--------------------------------------------------------------------------------
		LeadCount leadCount = new LeadCount();
		leadCount.setCount(new ArrayList<>());
		// Current Month
		Date[] dates = DateUtils.getCurrentMonth();
		List<Integer> currentMonth = reportRepository.getLeadCount(dates[0], dates[1]);
		if (currentMonth != null) {
			leadCount.getCount().add(currentMonth.get(0));
		}
		// Previous One Month
		Date[] dates1 = DateUtils.getPreviousOneMonth();
		List<Integer> previousOneMonth = reportRepository.getLeadCount(dates1[0], dates1[1]);
		if (previousOneMonth != null) {
			leadCount.getCount().add(previousOneMonth.get(0));
		}
		// Previous Second Month
		Date[] dates2 = DateUtils.getPreviousSecondMonth();
		List<Integer> previousSecondMonth = reportRepository.getLeadCount(dates2[0], dates2[1]);
		if (previousSecondMonth != null) {
			leadCount.getCount().add(previousSecondMonth.get(0));
		}
		// Previous Third Month
		Date[] dates3 = DateUtils.getPreviousThirdMonth();
		List<Integer> previousThirdMonth = reportRepository.getLeadCount(dates3[0], dates3[1]);
		if (previousThirdMonth != null) {
			leadCount.getCount().add(previousThirdMonth.get(0));
		}
		// Previous Fourth Month
		Date[] dates4 = DateUtils.getPreviousFourthMonth();
		List<Integer> previousFourthMonth = reportRepository.getLeadCount(dates4[0], dates4[1]);
		if (previousFourthMonth != null) {
			leadCount.getCount().add(previousFourthMonth.get(0));
		}
		// Previous Fifth Month
		Date[] dates5 = DateUtils.getPreviousFifthMonth();
		List<Integer> previousFifthMonth = reportRepository.getLeadCount(dates5[0], dates5[1]);
		if (previousFifthMonth != null) {
			leadCount.getCount().add(previousFifthMonth.get(0));
		}

		leadAndCustomer.setLead(leadCount.getCount());

		//Customer Count--------------------------------------------------------------------------------
		CustomerCount customerCount = new CustomerCount();
		customerCount.setCount(new ArrayList<>());
		// Current Month
		Date[] cdates = DateUtils.getCurrentMonth();
		List<Integer> ccurrentMonth = reportRepository.getCustomerCount(cdates[0], cdates[1]);
		if (ccurrentMonth != null) {
			customerCount.getCount().add(ccurrentMonth.get(0));
		}
		// Previous One Month
		Date[] cdates1 = DateUtils.getPreviousOneMonth();
		List<Integer> cpreviousOneMonth = reportRepository.getCustomerCount(cdates1[0], cdates1[1]);
		if (cpreviousOneMonth != null) {
			customerCount.getCount().add(cpreviousOneMonth.get(0));
		}
		// Previous Second Month
		Date[] cdates2 = DateUtils.getPreviousSecondMonth();
		List<Integer> cpreviousSecondMonth = reportRepository.getCustomerCount(cdates2[0], cdates2[1]);
		if (cpreviousSecondMonth != null) {
			customerCount.getCount().add(cpreviousSecondMonth.get(0));
		}
		// Previous Third Month
		Date[] cdates3 = DateUtils.getPreviousThirdMonth();
		List<Integer> cpreviousThirdMonth = reportRepository.getCustomerCount(cdates3[0], cdates3[1]);
		if (cpreviousThirdMonth != null) {
			customerCount.getCount().add(cpreviousThirdMonth.get(0));
		}
		// Previous Fourth Month
		Date[] cdates4 = DateUtils.getPreviousFourthMonth();
		List<Integer> cpreviousFourthMonth = reportRepository.getCustomerCount(cdates4[0], cdates4[1]);
		if (cpreviousFourthMonth != null) {
			customerCount.getCount().add(cpreviousFourthMonth.get(0));
		}
		// Previous Fifth Month
		Date[] cdates5 = DateUtils.getPreviousFifthMonth();
		List<Integer> cpreviousFifthMonth = reportRepository.getCustomerCount(cdates5[0], cdates5[1]);
		if (cpreviousFifthMonth != null) {
			customerCount.getCount().add(cpreviousFifthMonth.get(0));
		}

		leadAndCustomer.setCustomer(customerCount.getCount());

		return leadAndCustomer;
	}

}
