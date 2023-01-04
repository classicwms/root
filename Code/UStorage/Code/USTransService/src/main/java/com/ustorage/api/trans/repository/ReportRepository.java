package com.ustorage.api.trans.repository;

import com.ustorage.api.trans.model.agreement.Agreement;
import com.ustorage.api.trans.model.impl.*;
import com.ustorage.api.trans.model.leadcustomer.LeadCustomer;
import com.ustorage.api.trans.model.reports.ICustomerDropDown;
import com.ustorage.api.trans.model.reports.IKeyValuePair;
import com.ustorage.api.trans.model.reports.IStorageDropDown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Agreement, Long>,
		JpaSpecificationExecutor<Agreement> {

	

	@Query(value = "SELECT distinct tblleadcustomer.CUSTOMER_NAME \r\n"
			+ "FROM tblleadcustomer \r\n"
			+ "JOIN tblagreement ON tblleadcustomer.CUSTOMER_CODE=tblagreement.CUSTOMER_NAME \r\n"
			+ "WHERE tblagreement.CUSTOMER_NAME=:customerName \r\n"
			+ "AND tblleadcustomer.IS_DELETED=0 AND tblagreement.IS_DELETED=0", nativeQuery = true)
	public String getText(@Param("customerName") String customerName);

	//WorkOrderStatus Report
	@Query(value = "select tblworkorder.CTD_ON as createdOn, \n"+
					"tblworkorder.work_order_date as workOrderDate, \n"+
					"tblworkorder.WORK_ORDER_ID as workOrderId, \n"+
					"tblworkorder.WORK_ORDER_SBU as workOrderSbu, \n"+
					"tblworkorder.CUSTOMER_ID as customerId, \n"+
					"tblleadcustomer.CUSTOMER_NAME as customerName, \n"+
					"tblworkorder.STATUS as status, \n"+
					"tblworkorder.REMARKS as remarks, \n"+
					"tblworkorder.CREATED as created, \n"+
					"tblworkorder.PROCESSED_BY as processedBy, \n"+
					"tblworkorder.PROCESSED_TIME as processedTime, \n"+
					"tblworkorder.LEAD_TIME as leadTime \n"+
					"from tblworkorder \n"+
					"join tblleadcustomer on tblworkorder.CUSTOMER_ID=tblleadcustomer.CUSTOMER_CODE \n"+
					"where \n" +
					"(COALESCE(:workOrderId,null) IS NULL OR (tblworkorder.WORK_ORDER_ID IN (:workOrderId))) and \n"+
					"(COALESCE(:workOrderSbu,null) IS NULL OR (tblworkorder.WORK_ORDER_SBU IN (:workOrderSbu))) and \n"+
					"(COALESCE(:startDate,null) IS NULL OR (tblworkorder.WORK_ORDER_DATE between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"+
					"tblworkorder.is_deleted=0 and tblleadcustomer.is_deleted=0",nativeQuery = true)
	public List<WorkOrderStatusReportImpl>getWorkOrderStatus(
			@Param(value = "workOrderId") List<String> workOrderId,
			@Param(value = "workOrderSbu") List<String> workOrderSbu,
			@Param(value = "startDate") Date startDate,
			@Param(value = "endDate") Date endDate);

	//EfficiencyRecord Report
	@Query(value = "select tw.ctd_on as createdOn, \n"+
					"tw.processed_by as processedBy, \n"+
					"tw.work_order_date as workOrderDate, \n"+
					"tw.processed_time as processedTime, \n"+
					"tw.lead_time as leadTime, \n"+
					"tw.start_date as startDate, \n"+
					"tw.end_date as endDate, \n"+
					"tw.job_card_type as jobCardType, \n"+
					"tw.planned_hours as plannedHours, \n"+
					"(datediff(hour,tw.work_order_date,tw.end_date)-datediff(hour,tw.start_date,tw.end_date)) as workedHours \n"+
					"from tblworkorder tw \n"+
					"where \n"+
					"(COALESCE(:startDate,null) IS NULL OR (tw.WORK_ORDER_DATE between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"+
					"(COALESCE(:jobCardType,null) IS NULL OR (tw.JOB_CARD_TYPE IN (:jobCardType))) and \n"+
					"(COALESCE(:processedBy,null) IS NULL OR (tw.PROCESSED_BY IN (:processedBy))) and \n"+
					"tw.is_deleted=0",nativeQuery = true)
	public List<EfficiencyRecordImpl>getEfficiencyRecord(
			@Param(value ="startDate") Date startDate,
			@Param(value ="endDate") Date endDate,
			@Param(value = "jobCardType") List<String> jobCardType,
			@Param(value = "processedBy") List<String> ProcessedBy);

	//QuotationStatus Report
	@Query(value = "select tblquote.CTD_ON as quotationDate, \n"+
					"tblquote.REQUIREMENT as requirementType, \n"+
					"tblquote.ENQUIRY_REFERENCE_NUMBER as enquiryReferenceNumber, \n"+
					"tblquote.QUOTE_ID as quoteId, \n"+
					"tblquote.CUSTOMER_CODE as customerCode, \n"+
					"tblleadcustomer.CUSTOMER_NAME as customerName, \n"+
					"tblquote.MOBILE_NUMBER as mobileNumber, \n"+
					"tblquote.SBU as sbu, \n"+
					"tblquote.RENT as rent, \n"+
					"tblquote.STATUS as status, \n"+
					"tblquote.NOTES as notes\n"+
					"from tblquote \n"+
					"join tblleadcustomer on tblquote.customer_name=tblleadcustomer.customer_code \n"+
					"where \n" +
					"(COALESCE(:quoteId,null) IS NULL OR (tblquote.QUOTE_ID IN (:quoteId))) and \n"+
					"(COALESCE(:status,null) IS NULL OR (tblquote.STATUS IN (:status))) and \n"+
					"(COALESCE(:requirement,null) IS NULL OR (tblquote.REQUIREMENT IN (:requirement))) and \n"+
					"(COALESCE(:sbu,null) IS NULL OR (tblquote.SBU IN (:sbu))) and \n"+
					"tblquote.is_deleted=0 and tblleadcustomer.is_deleted=0",nativeQuery = true)
	public List<QuotationStatusImpl>getQuotationStatus(
			@Param(value ="quoteId") List<String> quoteId,
			@Param(value = "status") List<String> status,
			@Param(value = "requirement") List<String> requirementType,
			@Param(value = "sbu") List<String> sbu);

	//EnquiryStatus Report
	@Query(value = "select tblenquiry.CTD_ON as enquiryDate, \n"+
					"tblenquiry.REQUIREMENT_DETAIL as requirementType, \n"+
					"tblenquiry.ENQUIRY_ID as enquiryId, \n"+
					"tblleadcustomer.CUSTOMER_CODE as customerCode, \n"+
					"tblleadcustomer.CUSTOMER_NAME as customerName, \n"+
					"tblleadcustomer.MOBILE_NUMBER as mobileNumber, \n"+
					"tblenquiry.ENQUIRY_STORE_SIZE as enquiryStoreSize, \n"+
					"tblenquiry.ENQUIRY_NAME as enquiryName, \n"+
					"tblenquiry.ENQUIRY_STATUS as enquiryStatus, \n"+
					"tblenquiry.SBU as sbu, \n"+
					"tblenquiry.ENQUIRY_REMARKS as enquiryRemarks \n"+
					"from tblenquiry \n"+
					"join tblleadcustomer on tblenquiry.ENQUIRY_NAME=tblleadcustomer.CUSTOMER_NAME \n"+
					"where \n" +
					"(COALESCE(:enquiryId,null) IS NULL OR (tblenquiry.ENQUIRY_ID IN (:enquiryId))) and \n"+
					"(COALESCE(:enquiryStatus,null) IS NULL OR (tblenquiry.ENQUIRY_STATUS IN (:enquiryStatus))) and \n"+
					"(COALESCE(:requirementDetail,null) IS NULL OR (tblenquiry.REQUIREMENT_DETAIL IN (:requirementDetail))) and \n"+
					"(COALESCE(:sbu,null) IS NULL OR (tblenquiry.SBU IN (:sbu))) and \n"+
					"tblenquiry.is_deleted=0 and tblleadcustomer.is_deleted=0",nativeQuery = true)
			public List<EnquiryStatusImpl> getEnquiryStatus(
					@Param(value ="enquiryId") List<String> enquiryId,
					@Param(value = "enquiryStatus") List<String> enquiryStatus,
					@Param(value = "requirementDetail") List<String> requirementType,
					@Param(value = "sbu") List<String> sbu);

	//FillrateStatus Report
	@Query(value = "select \n"+
					"tblagreement.AGREEMENT_NUMBER as agreementNumber, \n"+
					"tblstorageunit.PHASE as phase, \n"+
					"tblstorageunit.ZONE as zone, \n"+
					"tblstorageunit.ROOM as room, \n"+
					"tblstorageunit.RACK as rack, \n"+
					"tblstorageunit.STORAGE_TYPE  as storageType, \n"+
					"tblstorageunit.BIN as bin, \n"+
					"tblstorageunit.ITEM_CODE as storeNumber, \n"+
					"tblstorageunit.PRICE_METER_SQAURE as priceMeterSquare, \n"+
					"tblstorageunit.AVAILABILITY as status, \n"+
					"tblleadcustomer.CUSTOMER_CODE as customerCode, \n"+
					"tblleadcustomer.CUSTOMER_NAME as customerName, \n"+
					"tblleadcustomer.MOBILE_NUMBER as mobileNumber, \n"+
					"tblleadcustomer.PHONE_NUMBER as phoneNumber, \n"+
					"tblstorageunit.REF_FIELD_2 as notes \n"+
					"from tblstorenumber \n"+
					"join tblagreement on tblagreement.AGREEMENT_NUMBER=tblstorenumber.AGREEMENT_NUMBER \n"+
					"join tblstorageunit on tblstorageunit.ITEM_CODE=tblstorenumber.STORE_NUMBER \n"+
					"join tblleadcustomer on tblleadcustomer.CUSTOMER_CODE=tblagreement.CUSTOMER_NAME \n"+
					"where \n" +
					"(COALESCE(:phase,null) IS NULL OR (tblstorageunit.PHASE IN (:phase))) and \n"+
					"(COALESCE(:storeNumber,null) IS NULL OR (tblstorageunit.ITEM_CODE IN (:storeNumber))) and \n"+
					"(COALESCE(:storageType,null) IS NULL OR (tblstorageunit.STORAGE_TYPE IN (:storageType))) and \n"+
					"(COALESCE(:availability,null) IS NULL OR (tblstorageunit.AVAILABILITY IN (:availability))) and \n"+
					"tblstorenumber.is_deleted=0 and tblstorageunit.is_deleted=0 and \n"+
					"tblagreement.is_deleted=0 and tblleadcustomer.is_deleted=0",nativeQuery = true)
	public List<FillrateStatusImpl> getFillrateStatus(
			@Param(value ="phase") List<String> phase,
			@Param(value = "storeNumber") List<String> storeNumber,
			@Param(value = "storageType") List<String> storageType,
			@Param(value = "availability") List<String> status);

	//ContractRenewalStatus Report
	@Query(value = "select \n"+
			"ta.start_date as agreementRenewalDate, \n"+
			"ta.agreement_number as agreementNumber, \n"+
			"tl.customer_name as customerName, \n"+
			"ta.phone_number as phoneNumber, \n"+
			"ta.secondary_number as secondaryNumber, \n"+
			"ts.store_number  as storeNumber, \n"+
			"ta.size as size, \n"+
			"tsu.storage_type as storageType, \n"+
			"tsu.phase as phase, \n"+
			"ta.total_rent as totalRent, \n"+
			"datediff(DAY,ta.start_date,(select end_date from tblagreement ta1 where agreement_number=ta.ref_field_4)) as fillrateStatus \n"+
			"from tblstorenumber ts \n"+
			"join tblagreement ta on ta.agreement_number=ts.agreement_number \n"+
			"join tblstorageunit tsu on tsu.item_code=ts.store_number \n"+
			"join tblleadcustomer tl on tl.customer_code=ta.customer_name \n"+
			"where \n" +
			"(COALESCE(:phase,null) IS NULL OR (tsu.PHASE IN (:phase))) and \n"+
			"(COALESCE(:storeNumber,null) IS NULL OR (tsu.ITEM_CODE IN (:storeNumber))) and \n"+
			"(COALESCE(:storageType,null) IS NULL OR (tsu.STORAGE_TYPE IN (:storageType))) and \n"+
			"ta.agreement_type='renew' and ts.is_deleted=0 and tsu.is_deleted=0 and \n"+
			"ta.is_deleted=0 and tl.is_deleted=0",nativeQuery = true)
	public List<ContractRenewalStatusImpl> getContractRenewalStatus(
			@Param(value ="phase") List<String> phase,
			@Param(value = "storeNumber") List<String> storeNumber,
			@Param(value = "storageType") List<String> storageType);

	//PaymentDueStatus Report
	@Query(value = "select \n"+
					"y.agreement_number as agreementNumber,\n" +
					"y.customer_name as customerName,\n" +
					"y.customer_code as customerCode,\n" +
					"y.civil_id_number as civilIdNumber,\n" +
					"y.phone_number as phoneNumber,\n" +
					"y.secondary_number as secondaryNumber,\n" +
					"y.location as location,\n" +
					"y.store_number as storeNumber,\n" +
					"y.size as size,\n" +
					"y.storage_type as storeType,\n" +
					"y.phase as phase,\n" +
					"y.rent as rent,\n" +
					"y.payment_terms as paymentTerms,\n" +
					"y.agreement_discount as agreementDiscount,\n" +
					"y.total_after_discount as totalAfterDiscount,\n" +
					"y.start_date as startDate,\n" +
					"y.end_date as endDate,\n" +
					"y.lastPaidDate as lastPaidDate,\n" +
					"y.dueDate as dueDate,\n" +
					"y.rent_period as rentPeriod,\n" +
					"y.total_rent as totalRent,\n" +
					"DATEDIFF(day,CURRENT_TIMESTAMP,y.dueDate) as dueDays,\n" +
					"y.totalPaidVoucherAmount as totalPaidVoucherAmount,\n" +
					"y.totalDueAmount as totalDueAmount,\n" +
					"y.nxtDAmount as nextDueAmount\n" +
					"from\n"+
					"(select *,\n"+
					"(case when x.totalDueAmount < x.nxtdueamount then x.totalDueAmount else x.nxtdueamount end) as nxtDAmount,\n"+
					"(case when x.dDate<=Current_timestamp then \n"+
					"case\n"+
						"when x.rent_period='Monthly' then Dateadd(month,1,x.dDate)\n"+
						"when x.rent_period='Weekly' then Dateadd(week,1,x.dDate)\n"+
						"when x.rent_period='Yearly' then Dateadd(YEAR,1,x.dDate)\n"+
						"when x.rent_period='Quarterly' then Dateadd(QUARTER,1,x.dDate)\n"+
						"when x.rent_period='Half yearly' then Dateadd(month,6,x.dDate)\n"+
						"else x.dDate end else x.dDate end) as dueDate\n"+
					"from\n"+
					"(select distinct\n"+
					"ta.agreement_number,tl.customer_name,tl.customer_code,ta.civil_id_number,ta.phone_number,\n"+
					"ta.secondary_number,ta.location,ta.store_number,ta.size,\n"+
					"tsu.storage_type,tsu.phase,ta.rent,ta.total_rent,\n"+
					"ta.payment_terms,ta.agreement_discount,ta.total_after_discount,\n"+
					"ta.rent_period,ta.start_date,ta.end_date,\n"+
					"sum(cast(tp.voucher_amount as bigint)) as totalPaidVoucherAmount,\n"+
					"ta.total_rent-sum(cast(tp.voucher_amount as bigint)) as totalDueAmount,\n"+
					"max(tp.paid_date) as lastPaidDate,\n"+
					"(case\n"+
					"when ta.rent_period='Monthly' then Dateadd(month,(case when (DATEDIFF(month,ta.start_date,CURRENT_TIMESTAMP))=0 then 1 else (DATEDIFF(month,ta.start_date,CURRENT_TIMESTAMP)) end),convert(datetime,ta.start_date,121))\n"+
					"when ta.rent_period='Weekly' then Dateadd(WEEK,(DATEDIFF(week,ta.start_date,CURRENT_TIMESTAMP)),convert(datetime,ta.start_date,121))\n"+
					"when ta.rent_period='Yearly' then Dateadd(YEAR,(DATEDIFF(YEAR,ta.start_date,CURRENT_TIMESTAMP)),convert(datetime,ta.start_date,121))\n"+
					"when ta.rent_period='Quarterly' then Dateadd(QUARTER,(DATEDIFF(QUARTER,ta.start_date,CURRENT_TIMESTAMP)),convert(datetime,ta.start_date,121))\n"+
					"when ta.rent_period='Half yearly' then Dateadd(MONTH,(DATEDIFF(month,ta.start_date,CURRENT_TIMESTAMP))+6,convert(datetime,ta.start_date,121))\n"+
					"when ta.payment_terms='Net 15 Days' then Dateadd(day,15,convert(datetime,ti.invoice_date,121))\n"+
					"else 0 end) as dDate,\n"+
						"(case\n"+
						"when ta.rent_period='Monthly' then ta.total_rent/12\n"+
						"when ta.rent_period='Weekly' then ta.total_rent/52\n"+
						"when ta.rent_period='Yearly' then ta.total_rent/1\n"+
						"when ta.rent_period='Quarterly' then ta.total_rent/3\n"+
						"when ta.rent_period='Half yearly' then ta.total_rent/6\n"+
						"when ta.payment_terms='Net 15 Days' then ta.total_rent\n"+
						"else 0 end) as nxtdueamount\n"+
			"from tblagreement as ta\n"+
			"join tblstorageunit as tsu on ta.store_number=tsu.item_code\n"+
			"join tblleadcustomer tl on tl.customer_code=ta.customer_name\n"+
			"join tblpaymentvoucher tp on tp.contract_number=ta.agreement_number\n"+
			"join tblinvoice ti on ti.customer_id=ta.customer_name\n"+
			"where \n" +
			"(COALESCE(:agreementNumber,null) IS NULL OR (ta.AGREEMENT_NUMBER IN (:agreementNumber))) and \n"+
			"(COALESCE(:customerName,null) IS NULL OR (tl.CUSTOMER_NAME IN (:customerName))) and \n"+
			"(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"+
			"(COALESCE(:phoneNumber,null) IS NULL OR (tl.MOBILE_NUMBER IN (:phoneNumber))) and \n"+
			"(COALESCE(:secondaryNumber,null) IS NULL OR (tl.PHONE_NUMBER IN (:secondaryNumber))) and \n"+
			"(COALESCE(:civilId,null) IS NULL OR (tl.CIVIL_ID IN (:civilId))) and \n"+
			"(COALESCE(:startDate,null) IS NULL OR (ta.CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"+
			"ta.is_deleted=0 and tsu.is_deleted=0 and tp.is_deleted=0 and ti.is_deleted=0\n"+
			"group by ta.agreement_number,tl.customer_name,tl.customer_code,ta.civil_id_number,ta.phone_number,\n"+
			"ta.secondary_number,ta.location,ta.store_number,ta.size,tsu.storage_type,\n"+
			"tsu.phase,ta.rent,ta.total_rent,ta.payment_terms,ta.agreement_discount,\n"+
			"ta.total_after_discount,tp.voucher_amount,ta.rent_period,\n"+
			"ta.start_date,ta.end_date,ti.invoice_date,tp.paid_date) x) y",nativeQuery = true)
	public List<PaymentDueStatusReportImpl> getPaymentDueStatus(
			@Param(value ="agreementNumber") List<String> agreementNumber,
			@Param(value = "customerName") List<String> customerName,
			@Param(value = "customerCode") List<String> customerCode,
			@Param(value = "phoneNumber") List<String> phoneNumber,
			@Param(value = "secondaryNumber") List<String> secondaryNumber,
			@Param(value = "civilId") List<String> civilId,
			@Param(value ="startDate") Date startDate,
			@Param(value ="endDate") Date endDate);


	//CustomerDropDown
	@Query (value = "SELECT distinct lc.CUSTOMER_CODE as customerCode, \r\n"
			+ "lc.CUSTOMER_NAME as customerName, \r\n"
			+ "lc.CIVIL_ID as civilId \r\n"
			+ "FROM tblleadcustomer lc \r\n"
			+ "WHERE lc.IS_DELETED = 0", nativeQuery = true)
	public List<ICustomerDropDown> getCustomerDropDownList ();

	//Document status-customer
	@Query (value = "SELECT tl.CUSTOMER_CODE as id, \r\n"
			+ "tl.CUSTOMER_NAME as name, \r\n"
			+ "tl.CUSTOMER_GROUP as cgroup, \r\n"
			+ "tl.EMAIL as email, \r\n"
			+ "tl.MOBILE_NUMBER as mobile, \r\n"
			+ "tl.PHONE_NUMBER as phone, \r\n"
			+ "tl.STATUS as status, \r\n"
			+ "tl.TYPE as type, \r\n"
			+ "tl.CTD_ON as createdOn, \r\n"
			+ "tl.CIVIL_ID as civilId \r\n"
			+ "FROM tblleadcustomer tl \r\n"
			+ "WHERE \r\n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "(COALESCE(:startDate,null) IS NULL OR (tl.CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"
			+ "tl.IS_DELETED = 0", nativeQuery = true)
	public List<DocumentStatusImpl> getCustomerName (@Param(value = "customerCode") List<String> customerCode,
													 @Param(value ="startDate") Date startDate,
													 @Param(value ="endDate") Date endDate);

	//Document status-agreement
	@Query (value = "SELECT ta.AGREEMENT_NUMBER AS documentNumber, ta.TOTAL_RENT AS amount,\r\n"
			+ "ta.AGREEMENT_TYPE as type, tl.CUSTOMER_NAME as customerName,tl.CUSTOMER_CODE as customerId, \r\n"
			+ "ta.LOCATION as location, ta.QUOTE_NUMBER as code, \r\n"
			+ "ta.RENT_PERIOD as Note, ta.END_DATE as endDate, \r\n"
			+ "ta.CTD_ON as documentDate, ta.START_DATE as startDate, \r\n"
			+ "ta.EMAIL as email, ta.PHONE_NUMBER as mobile, \r\n"
			+ "ta.SECONDARY_NUMBER as phone, ta.CIVIL_ID_NUMBER as civilId, \r\n"
			+ "ta.STATUS as status, ta.NOTES as remark,ts.store_number as storeNumber \r\n"
			+ "FROM tblstorenumber ts \r\n"
			+ "JOIN tblagreement ta ON ta.AGREEMENT_NUMBER=ts.AGREEMENT_NUMBER \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=ta.CUSTOMER_NAME \r\n"
			+ "WHERE \n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "(COALESCE(:startDate,null) IS NULL OR (ta.CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"
			+ "ta.IS_DELETED = 0", nativeQuery = true)
	public List<DocumentStatusImpl> getAgreemnt (
			@Param(value = "customerCode") List<String> customerCode,
			@Param(value ="startDate") Date startDate,
			@Param(value ="endDate") Date endDate);

	//Document status-invoice
	@Query (value = "SELECT ti.INVOICE_NUMBER AS documentNumber, ti.INVOICE_AMOUNT AS amount,\r\n"
			+ "tl.CUSTOMER_NAME as customerName,tl.CUSTOMER_CODE as customerId, ti.DOCUMENT_NUMBER as code, \r\n"
			+ "ti.INVOICE_DATE as documentDate, ti.DOCUMENT_START_DATE as startDate, \r\n"
			+ "ti.DOCUMENT_END_DATE as endDate, ti.REF_FIELD_3 as location,ti.REF_FIELD_4 as storeNumber, \r\n"
			+ "ti.UNIT as type, tl.EMAIL as email, \r\n"
			+ "tl.MOBILE_NUMBER as mobile, tl.PHONE_NUMBER as phone, \r\n"
			+ "ti.INVOICE_DOCUMENT_STATUS as status, ti.REMARKS as remarks \r\n"
			+ "FROM tblinvoice ti \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=ti.CUSTOMER_ID \r\n"
			+ "WHERE \n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "(COALESCE(:startDate,null) IS NULL OR (ti.INVOICE_DATE between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"
			+ "ti.IS_DELETED = 0", nativeQuery = true)
	public List<DocumentStatusImpl> getInvice (
			@Param(value = "customerCode") List<String> customerCode,
			@Param(value ="startDate") Date startDate,
			@Param(value ="endDate") Date endDate);

	//Document status-payment voucher
	@Query (value = "SELECT tp.VOUCHER_ID AS documentNumber, tp.VOUCHER_AMOUNT AS amount,\r\n"
			+ "tl.CUSTOMER_NAME as customerName,tl.CUSTOMER_CODE as customerId, tp.CONTRACT_NUMBER as code, \r\n"
			+ "tp.VOUCHER_DATE as documentDate, \r\n"
			+ "tp.PAID_DATE as note, tp.START_DATE as startDate, \r\n"
			+ "tp.PERIOD as cgroup, tp.MODE_OF_PAYMENT as type, \r\n"
			+ "tp.END_DATE as endDate,tp.STORE_NUMBER as storeNumber, \r\n"
			+ "tl.MOBILE_NUMBER as mobile, tl.EMAIL as email, \r\n"
			+ "tp.VOUCHER_STATUS as status, tp.REMARKS as remarks \r\n"
			+ "FROM tblpaymentvoucher tp \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=tp.CUSTOMER_NAME \r\n"
			+ "WHERE \n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "(COALESCE(:startDate,null) IS NULL OR (tp.VOUCHER_DATE between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"
			+ "tp.IS_DELETED = 0", nativeQuery = true)
	public List<DocumentStatusImpl> getPaymnt (
			@Param(value = "customerCode") List<String> customerCode,
			@Param(value ="startDate") Date startDate,
			@Param(value ="endDate") Date endDate);

	//Document status-work order
	@Query (value = "SELECT tw.WORK_ORDER_ID AS documentNumber, sum(ti.ITEM_SERVICE_TOTAL) AS amount,\r\n"
			+ "tw.CODE_ID as code, tw.CREATED as cgroup, \r\n"
			+ "tw.START_DATE as startDate, \r\n"
			+ "tw.END_DATE as endDate, \r\n"
			+ "tw.PROCESSED_BY as location, tw.WORK_ORDER_DATE as documentDate, \r\n"
			+ "tw.PLANNED_HOURS as note, tl.CUSTOMER_NAME as customerName,tl.CUSTOMER_CODE as customerId, \r\n"
			+ "tl.MOBILE_NUMBER as mobile, tl.EMAIL as email, \r\n"
			+ "tw.STATUS as status, tw.REMARKS as remarks \r\n"
			+ "FROM tblitemservice ti \r\n"
			+ "JOIN tblworkorder tw on tw.WORK_ORDER_ID =ti.WORK_ORDER_ID \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=tw.CUSTOMER_ID \r\n"
			+ "WHERE \n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "(COALESCE(:startDate,null) IS NULL OR (tw.WORK_ORDER_DATE between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"
			+ "tw.IS_DELETED = 0 \n"
			+ "group by tw.WORK_ORDER_ID,tw.CODE_ID,tw.CREATED,\n"
			+ "tw.START_DATE,tw.END_DATE,tw.PROCESSED_BY,tw.WORK_ORDER_DATE,tw.PLANNED_HOURS,\n"
			+ "tl.CUSTOMER_NAME, tl.MOBILE_NUMBER,tl.EMAIL,tw.STATUS,tw.REMARKS,tl.CUSTOMER_CODE;", nativeQuery = true)
	public List<DocumentStatusImpl> getWorkordr (
			@Param(value = "customerCode") List<String> customerCode,
			@Param(value ="startDate") Date startDate,
			@Param(value ="endDate") Date endDate);

	//Document status-Quotation
	@Query (value = "SELECT tq.QUOTE_ID AS documentNumber, tq.RENT AS amount,\r\n"
			+ "tq.CTD_ON as documentDate, tq.CUSTOMER_GROUP as cgroup, \r\n"
			+ "tq.ENQUIRY_REFERENCE_NUMBER as code, tq.REF_FIELD_1 as type, \r\n"
			+ "tq.REQUIREMENT as serviceType, tq.SBU as location, \r\n"
			+ "tq.STATUS as status, tl.CUSTOMER_NAME as customerName,tl.CUSTOMER_CODE as customerId, \r\n"
			+ "tl.MOBILE_NUMBER as mobile, tl.EMAIL as email, \r\n"
			+ "tq.NOTES as remarks \r\n"
			+ "FROM tblquote tq \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=tq.CUSTOMER_CODE \r\n"
			+ "WHERE \n"
			+"(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "(COALESCE(:startDate,null) IS NULL OR (tq.CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"
			+ "tq.IS_DELETED = 0", nativeQuery = true)
	public List<DocumentStatusImpl> getQote (
			@Param(value = "customerCode") List<String> customerCode,
			@Param(value ="startDate") Date startDate,
			@Param(value ="endDate") Date endDate);

	//Document status-Enquiry
	@Query (value = "SELECT te.ENQUIRY_ID AS documentNumber, te.ENQUIRY_STORE_SIZE AS amount,\r\n"
			+ "te.CTD_ON as documentDate, te.CUSTOMER_GROUP as cgroup, \r\n"
			+ "te.EMAIL as email, te.ENQUIRY_MOBILE_NUMBER as mobile, \r\n"
			+ "tl.CUSTOMER_NAME as customerName,tl.CUSTOMER_CODE as customerId, \r\n"
			+ "te.REF_FIELD_1 as type, te.SBU as location, \r\n"
			+ "te.REQUIREMENT_DETAIL as serviceType, \r\n"
			+ "te.ENQUIRY_STATUS as status, te.ENQUIRY_REMARKS as remarks \r\n"
			+ "FROM tblenquiry te \r\n"
			+ "JOIN tblquote tq on tq.ENQUIRY_REFERENCE_NUMBER=te.ENQUIRY_ID \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=tq.CUSTOMER_CODE \r\n"
			+ "WHERE \n"
			+"(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "(COALESCE(:startDate,null) IS NULL OR (te.CTD_ON between COALESCE(CONVERT(VARCHAR(255), :startDate), null) and COALESCE(CONVERT(VARCHAR(255), :endDate), null))) and \n"
			+ "te.IS_DELETED = 0", nativeQuery = true)
	public List<DocumentStatusImpl> getEnqury (
			@Param(value = "customerCode") List<String> customerCode,
			@Param(value ="startDate") Date startDate,
			@Param(value ="endDate") Date endDate);

	//StorageDropDown
	@Query (value = "SELECT distinct tsu.ITEM_CODE as storeNumber, \r\n"
			+ "tsu.CODE_ID as storeId, \r\n"
			+ "tsu.description as description \r\n"
			+ "FROM tblstorageunit tsu \r\n"
			+ "WHERE tsu.IS_DELETED = 0", nativeQuery = true)
	public List<IStorageDropDown> getStorageDropDownList ();

	//Customer Detail-Customer Id and Name
	@Query (value = "SELECT tl.CUSTOMER_CODE AS documentNumber, tl.CUSTOMER_NAME AS notes\r\n"
			+ "FROM tblleadcustomer tl \r\n "
			+ "WHERE \n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "tl.IS_DELETED = 0", nativeQuery = true)
	public List<IKeyValuePair> getClientNameList (
			@Param(value = "customerCode") String customerCode);

	//Customer Detail-Agreement
	@Query (value = "SELECT ta.AGREEMENT_NUMBER AS documentNumber, ta.TOTAL_RENT AS total,\r\n"
			+ "ta.STATUS as status, ta.NOTES as notes, ta.CTD_ON as documentDate \r\n"
			+ "FROM tblagreement ta \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=ta.CUSTOMER_NAME \r\n"
			+ "WHERE \n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "ta.IS_DELETED = 0", nativeQuery = true)
	public List<IKeyValuePair> getAgreement (
			@Param(value = "customerCode") String customerCode);

	//Customer Detail-Invoice
	@Query (value = "SELECT ti.INVOICE_NUMBER AS documentNumber, ti.INVOICE_AMOUNT AS total,\r\n"
			+ "ti.INVOICE_DOCUMENT_STATUS as status, ti.REMARKS as notes, ti.INVOICE_DATE as documentDate \r\n"
			+ "FROM tblinvoice ti \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=ti.CUSTOMER_ID \r\n"
			+ "WHERE \n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "ti.IS_DELETED = 0", nativeQuery = true)
	public List<IKeyValuePair> getInvoice (
			@Param(value = "customerCode") String customerCode);

	//Customer Detail-Payment Voucher
	@Query (value = "SELECT tp.VOUCHER_ID AS documentNumber, tp.VOUCHER_AMOUNT AS total,\r\n"
			+ "tp.VOUCHER_STATUS as status, tp.REMARKS as notes, tp.VOUCHER_DATE as documentDate \r\n"
			+ "FROM tblpaymentvoucher tp \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=tp.CUSTOMER_NAME \r\n"
			+ "WHERE \n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "tp.IS_DELETED = 0", nativeQuery = true)
	public List<IKeyValuePair> getPayment (
			@Param(value = "customerCode") String customerCode);

	//Customer Detail-Work Order
	@Query (value = "SELECT tw.WORK_ORDER_ID AS documentNumber, sum(ti.ITEM_SERVICE_TOTAL) AS total,\r\n"
			+ "tw.STATUS as status, tw.REMARKS as notes, tw.WORK_ORDER_DATE as documentDate \r\n"
			+ "FROM tblitemservice ti \r\n"
			+ "JOIN tblworkorder tw on tw.WORK_ORDER_ID =ti.WORK_ORDER_ID \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=tw.CUSTOMER_ID \r\n"
			+ "WHERE \n"
			+ "(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "tw.IS_DELETED = 0"
			+ "group by tw.WORK_ORDER_ID,tw.STATUS,tw.REMARKS,tw.WORK_ORDER_DATE", nativeQuery = true)
	public List<IKeyValuePair> getWorkorder (
			@Param(value = "customerCode") String customerCode);

	//Customer Detail-Quotation
		@Query (value = "SELECT tq.QUOTE_ID AS documentNumber, tq.RENT AS total,\r\n"
			+ "tq.STATUS as status, tq.NOTES as notes, tq.CTD_ON as documentDate \r\n"
			+ "FROM tblquote tq \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=tq.CUSTOMER_CODE \r\n"
			+ "WHERE \n"
			+"(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "tq.IS_DELETED = 0", nativeQuery = true)
	public List<IKeyValuePair> getQuote (
			@Param(value = "customerCode") String customerCode);

	//Customer Detail-Enquiry
	@Query (value = "SELECT te.ENQUIRY_ID AS documentNumber, te.ENQUIRY_STORE_SIZE AS total,\r\n"
			+ "te.ENQUIRY_STATUS as status, te.ENQUIRY_REMARKS as notes, te.CTD_ON as documentDate \r\n"
			+ "FROM tblenquiry te \r\n"
			+ "JOIN tblquote tq on tq.ENQUIRY_REFERENCE_NUMBER=te.ENQUIRY_ID \r\n"
			+ "JOIN tblleadcustomer tl ON tl.CUSTOMER_CODE=tq.CUSTOMER_CODE \r\n"
			+ "WHERE \n"
			+"(COALESCE(:customerCode,null) IS NULL OR (tl.CUSTOMER_CODE IN (:customerCode))) and \n"
			+ "te.IS_DELETED = 0", nativeQuery = true)
	public List<IKeyValuePair> getEnquiry (
			@Param(value = "customerCode") String customerCode);
}