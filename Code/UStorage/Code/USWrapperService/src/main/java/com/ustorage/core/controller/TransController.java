package com.ustorage.core.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import com.ustorage.core.batch.scheduler.BatchJobScheduler;
import com.ustorage.core.model.masters.*;
import com.ustorage.core.model.reports.*;
import com.ustorage.core.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ustorage.core.model.trans.*;

import com.ustorage.core.service.TransService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Transaction Service" }, value = "TransactionService Operations related to TransactionServiceController")
@SwaggerDefinition(tags = { @Tag(name = "Trans", description = "Operations related to TransactionService") })
@RequestMapping("/us-trans-service")
@RestController
public class TransController {

	@Autowired
	TransService transService;

	@Autowired
	FileStorageService fileStorageService;

	@Autowired
	BatchJobScheduler batchJobScheduler;

	//-----------------------------------Agreement------------------------------------------------------------
	@ApiOperation(response = Agreement.class, value = "Get all Agreement details") // label for swagger
	@GetMapping("/operations/agreement")
	public ResponseEntity<?> getAllAgreement(@RequestParam String authToken) {
		Agreement[] agreementList = transService.getAllAgreement(authToken);
		return new ResponseEntity<>(agreementList, HttpStatus.OK);
	}

	@ApiOperation(response = GAgreement.class, value = "Get a Agreement") // label for swagger
	@GetMapping("/operations/agreement/{agreementNumber}")
	public ResponseEntity<?> getAgreement(@PathVariable String agreementNumber,@RequestParam String authToken) {
		GAgreement dbAgreement = transService.getAgreement(agreementNumber, authToken);
		return new ResponseEntity<>(dbAgreement, HttpStatus.OK);
	}

	@ApiOperation(response = Agreement.class, value = "Create Agreement") // label for swagger
	@PostMapping("/operations/agreement")
	public ResponseEntity<?> postAgreement(@Valid @RequestBody AddAgreement newAgreement,
										   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Agreement createdAgreement = transService.createAgreement(newAgreement, loginUserID, authToken);
		return new ResponseEntity<>(createdAgreement, HttpStatus.OK);
	}

	@ApiOperation(response = Agreement.class, value = "Update Agreement") // label for swagger
	@PatchMapping("/operations/agreement/{agreement}")
	public ResponseEntity<?> patchAgreement(@PathVariable String agreement,
											@RequestParam String loginUserID,
											@Valid @RequestBody UpdateAgreement updateAgreement,
											@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Agreement updatedAgreement = transService.updateAgreement(agreement, loginUserID,
				updateAgreement, authToken);
		return new ResponseEntity<>(updatedAgreement, HttpStatus.OK);
	}

	@ApiOperation(response = Agreement.class, value = "Delete Agreement") // label for swagger
	@DeleteMapping("/operations/agreement/{agreement}")
	public ResponseEntity<?> deleteAgreement(@PathVariable String agreement, @RequestParam String loginUserID,
											 @RequestParam String authToken) {
		transService.deleteAgreement(agreement, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//Find
	@ApiOperation(response = GAgreement[].class, value = "Find Agreement") // label for swagger
	@PostMapping("/operations/agreement/find")
	public GAgreement[] findAgreement(@RequestBody FindAgreement findAgreement,
									 @RequestParam String authToken)
			throws Exception {
		return transService.findAgreement(findAgreement, authToken);
	}
	//Find
	@ApiOperation(response = GAgreement[].class, value = "Find StoreNumber") // label for swagger
	@PostMapping("/operations/agreement/findStoreNumber")
	public GAgreement[] findStoreNumber(@RequestBody FindStoreNumber findStoreNumber,
									  @RequestParam String authToken)
			throws Exception {
		return transService.findStoreNumber(findStoreNumber, authToken);
	}
	//-----------------------------------Consumables------------------------------------------------------------
	@ApiOperation(response = Consumables.class, value = "Get all Consumables details") // label for swagger
	@GetMapping("/master/consumables")
	public ResponseEntity<?> getAllConsumables(@RequestParam String authToken) {
		Consumables[] consumablesList = transService.getAllConsumables(authToken);
		return new ResponseEntity<>(consumablesList, HttpStatus.OK);
	}

	@ApiOperation(response = Consumables.class, value = "Get a Consumables") // label for swagger
	@GetMapping("/master/consumables/{consumablesId}")
	public ResponseEntity<?> getConsumables(@PathVariable String consumablesId,@RequestParam String authToken) {
		Consumables dbConsumables = transService.getConsumables(consumablesId, authToken);
		return new ResponseEntity<>(dbConsumables, HttpStatus.OK);
	}

	@ApiOperation(response = Consumables.class, value = "Create Consumables") // label for swagger
	@PostMapping("/master/consumables")
	public ResponseEntity<?> postConsumables(@Valid @RequestBody AddConsumables newConsumables,
											 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Consumables createdConsumables = transService.createConsumables(newConsumables, loginUserID, authToken);
		return new ResponseEntity<>(createdConsumables, HttpStatus.OK);
	}

	@ApiOperation(response = Consumables.class, value = "Update Consumables") // label for swagger
	@PatchMapping("/master/consumables/{consumables}")
	public ResponseEntity<?> patchConsumables(@PathVariable String consumables,
											  @RequestParam String loginUserID,
											  @Valid @RequestBody UpdateConsumables updateConsumables,
											  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Consumables updatedConsumables = transService.updateConsumables(consumables, loginUserID,
				updateConsumables, authToken);
		return new ResponseEntity<>(updatedConsumables, HttpStatus.OK);
	}

	@ApiOperation(response = Consumables.class, value = "Delete Consumables") // label for swagger
	@DeleteMapping("/master/consumables/{consumables}")
	public ResponseEntity<?> deleteConsumables(@PathVariable String consumables, @RequestParam String loginUserID,
											   @RequestParam String authToken) {
		transService.deleteConsumables(consumables, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = Consumables[].class, value = "Find Consumables") // label for swagger
	@PostMapping("/master/consumables/find")
	public Consumables[] findConsumables(@RequestBody FindConsumables findConsumables,
										 @RequestParam String authToken)
			throws Exception {
		return transService.findConsumables(findConsumables, authToken);
	}
	//-----------------------------------Enquiry------------------------------------------------------------
	@ApiOperation(response = Enquiry.class, value = "Get all Enquiry details") // label for swagger
	@GetMapping("/crm/enquiry")
	public ResponseEntity<?> getAllEnquiry(@RequestParam String authToken) {
		Enquiry[] enquiryList = transService.getAllEnquiry(authToken);
		return new ResponseEntity<>(enquiryList, HttpStatus.OK);
	}

	@ApiOperation(response = Enquiry.class, value = "Get a Enquiry") // label for swagger
	@GetMapping("/crm/enquiry/{enquiryId}")
	public ResponseEntity<?> getEnquiry(@PathVariable String enquiryId,@RequestParam String authToken) {
		Enquiry dbEnquiry = transService.getEnquiry(enquiryId, authToken);
		return new ResponseEntity<>(dbEnquiry, HttpStatus.OK);
	}

	@ApiOperation(response = Enquiry.class, value = "Create Enquiry") // label for swagger
	@PostMapping("/crm/enquiry")
	public ResponseEntity<?> postEnquiry(@Valid @RequestBody AddEnquiry newEnquiry,
										 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Enquiry createdEnquiry = transService.createEnquiry(newEnquiry, loginUserID, authToken);
		return new ResponseEntity<>(createdEnquiry, HttpStatus.OK);
	}

	@ApiOperation(response = Enquiry.class, value = "Update Enquiry") // label for swagger
	@PatchMapping("/crm/enquiry/{enquiry}")
	public ResponseEntity<?> patchEnquiry(@PathVariable String enquiry,
										  @RequestParam String loginUserID,
										  @Valid @RequestBody UpdateEnquiry updateEnquiry,
										  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Enquiry updatedEnquiry = transService.updateEnquiry(enquiry, loginUserID,
				updateEnquiry, authToken);
		return new ResponseEntity<>(updatedEnquiry, HttpStatus.OK);
	}

	@ApiOperation(response = Enquiry.class, value = "Delete Enquiry") // label for swagger
	@DeleteMapping("/crm/enquiry/{enquiry}")
	public ResponseEntity<?> deleteEnquiry(@PathVariable String enquiry, @RequestParam String loginUserID,
										   @RequestParam String authToken) {
		transService.deleteEnquiry(enquiry, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = Enquiry[].class, value = "Find Enquiry") // label for swagger
	@PostMapping("/crm/enquiry/find")
	public Enquiry[] findEnquiry(@RequestBody FindEnquiry findEnquiry,
								 @RequestParam String authToken)
			throws Exception {
		return transService.findEnquiry(findEnquiry, authToken);
	}
	//-----------------------------------Invoice------------------------------------------------------------
	@ApiOperation(response = Invoice.class, value = "Get all Invoice details") // label for swagger
	@GetMapping("/operations/invoice")
	public ResponseEntity<?> getAllInvoice(@RequestParam String authToken) {
		Invoice[] invoiceList = transService.getAllInvoice(authToken);
		return new ResponseEntity<>(invoiceList, HttpStatus.OK);
	}

	@ApiOperation(response = Invoice.class, value = "Get a Invoice") // label for swagger
	@GetMapping("/operations/invoice/{invoiceId}")
	public ResponseEntity<?> getInvoice(@PathVariable String invoiceId,@RequestParam String authToken) {
		Invoice dbInvoice = transService.getInvoice(invoiceId, authToken);
		return new ResponseEntity<>(dbInvoice, HttpStatus.OK);
	}

	@ApiOperation(response = Invoice.class, value = "Create Invoice") // label for swagger
	@PostMapping("/operations/invoice")
	public ResponseEntity<?> postInvoice(@Valid @RequestBody AddInvoice newInvoice,
										 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Invoice createdInvoice = transService.createInvoice(newInvoice, loginUserID, authToken);
		return new ResponseEntity<>(createdInvoice, HttpStatus.OK);
	}

	@ApiOperation(response = Invoice.class, value = "Update Invoice") // label for swagger
	@PatchMapping("/operations/invoice/{invoice}")
	public ResponseEntity<?> patchInvoice(@PathVariable String invoice,
										  @RequestParam String loginUserID,
										  @Valid @RequestBody UpdateInvoice updateInvoice,
										  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Invoice updatedInvoice = transService.updateInvoice(invoice, loginUserID,
				updateInvoice, authToken);
		return new ResponseEntity<>(updatedInvoice, HttpStatus.OK);
	}

	@ApiOperation(response = Invoice.class, value = "Delete Invoice") // label for swagger
	@DeleteMapping("/operations/invoice/{invoice}")
	public ResponseEntity<?> deleteInvoice(@PathVariable String invoice, @RequestParam String loginUserID,
										   @RequestParam String authToken) {
		transService.deleteInvoice(invoice, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = Invoice[].class, value = "Find Invoice") // label for swagger
	@PostMapping("/operations/invoice/find")
	public Invoice[] findInvoice(@RequestBody FindInvoice findInvoice,
								 @RequestParam String authToken)
			throws Exception {
		return transService.findInvoice(findInvoice, authToken);
	}
	//-----------------------------------LeadCustomer------------------------------------------------------------
	@ApiOperation(response = LeadCustomer.class, value = "Get all LeadCustomer details") // label for swagger
	@GetMapping("/master/leadcustomer")
	public ResponseEntity<?> getAllLeadCustomer(@RequestParam String authToken) {
		LeadCustomer[] leadCustomerList = transService.getAllLeadCustomer(authToken);
		return new ResponseEntity<>(leadCustomerList, HttpStatus.OK);
	}

	@ApiOperation(response = LeadCustomer.class, value = "Get a LeadCustomer") // label for swagger
	@GetMapping("/master/leadcustomer/{leadCustomerId}")
	public ResponseEntity<?> getLeadCustomer(@PathVariable String leadCustomerId, @RequestParam String authToken) {
		LeadCustomer dbLeadCustomer = transService.getLeadCustomer(leadCustomerId, authToken);
		return new ResponseEntity<>(dbLeadCustomer, HttpStatus.OK);
	}

	@ApiOperation(response = LeadCustomer.class, value = "Create LeadCustomer") // label for swagger
	@PostMapping("/master/leadcustomer")
	public ResponseEntity<?> postLeadCustomer(@Valid @RequestBody AddLeadCustomer newLeadCustomer,
											  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		LeadCustomer createdLeadCustomer = transService.createLeadCustomer(newLeadCustomer, loginUserID, authToken);
		return new ResponseEntity<>(createdLeadCustomer, HttpStatus.OK);
	}

	@ApiOperation(response = LeadCustomer.class, value = "Update LeadCustomer") // label for swagger
	@PatchMapping("/master/leadcustomer/{leadCustomer}")
	public ResponseEntity<?> patchLeadCustomer(@PathVariable String leadCustomer,
											   @RequestParam String loginUserID,
											   @Valid @RequestBody UpdateLeadCustomer updateLeadCustomer,
											   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		LeadCustomer updatedLeadCustomer = transService.updateLeadCustomer(leadCustomer, loginUserID,
				updateLeadCustomer, authToken);
		return new ResponseEntity<>(updatedLeadCustomer, HttpStatus.OK);
	}

	@ApiOperation(response = LeadCustomer.class, value = "Delete LeadCustomer") // label for swagger
	@DeleteMapping("/master/leadcustomer/{leadCustomer}")
	public ResponseEntity<?> deleteLeadCustomer(@PathVariable String leadCustomer, @RequestParam String loginUserID,
												@RequestParam String authToken) {
		transService.deleteLeadCustomer(leadCustomer, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = LeadCustomer[].class, value = "Find LeadCustomer") // label for swagger
	@PostMapping("/master/leadcustomer/find")
	public LeadCustomer[] findLeadCustomer(@RequestBody FindLeadCustomer findLeadCustomer,
											  @RequestParam String authToken)
			throws Exception {
		return transService.findLeadCustomer(findLeadCustomer, authToken);
	}

	//-----------------------------------ConsumablePurchase------------------------------------------------------------
	@ApiOperation(response = ConsumablePurchase.class, value = "Get all ConsumablePurchase details") // label for swagger
	@GetMapping("/master/consumablepurchase")
	public ResponseEntity<?> getAllConsumablePurchase(@RequestParam String authToken) {
		ConsumablePurchase[] consumablePurchaseList = transService.getAllConsumablePurchase(authToken);
		return new ResponseEntity<>(consumablePurchaseList, HttpStatus.OK);
	}

	@ApiOperation(response = ConsumablePurchase.class, value = "Get a ConsumablePurchase") // label for swagger
	@GetMapping("/master/consumablepurchase/{consumablePurchaseId}")
	public ResponseEntity<?> getConsumablePurchase(@PathVariable String consumablePurchaseId, @RequestParam String authToken) {
		ConsumablePurchase[] dbConsumablePurchase = transService.getConsumablePurchase(consumablePurchaseId, authToken);
		return new ResponseEntity<>(dbConsumablePurchase, HttpStatus.OK);
	}

	@ApiOperation(response = ConsumablePurchase.class, value = "Create ConsumablePurchase") // label for swagger
	@PostMapping("/master/consumablepurchase")
	public ResponseEntity<?> postConsumablePurchase(@Valid @RequestBody List<AddConsumablePurchase> newConsumablePurchase,
											   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		ConsumablePurchase[] createdConsumablePurchase = transService.createConsumablePurchase(newConsumablePurchase, loginUserID, authToken);
		return new ResponseEntity<>(createdConsumablePurchase, HttpStatus.OK);
	}

	@ApiOperation(response = ConsumablePurchase.class, value = "Update ConsumablePurchase") // label for swagger
	@PatchMapping("/master/consumablepurchase")
	public ResponseEntity<?> patchConsumablePurchase(@RequestBody List<UpdateConsumablePurchase> updateConsumablePurchase,
												@RequestParam String loginUserID,
												@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ConsumablePurchase[] createdConsumablePurchase = transService.updateConsumablePurchase(updateConsumablePurchase,loginUserID, authToken);
		return new ResponseEntity<>(createdConsumablePurchase, HttpStatus.OK);
	}

	@ApiOperation(response = ConsumablePurchase.class, value = "Delete ConsumablePurchase") // label for swagger
	@DeleteMapping("/master/consumablepurchase/{consumablePurchase}")
	public ResponseEntity<?> deleteConsumablePurchase(@PathVariable String consumablePurchase, @RequestParam String loginUserID,
												 @RequestParam String authToken) {
		transService.deleteConsumablePurchase(consumablePurchase, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------PaymentVoucher------------------------------------------------------------
	@ApiOperation(response = PaymentVoucher.class, value = "Get all PaymentVoucher details") // label for swagger
	@GetMapping("/operations/paymentvoucher")
	public ResponseEntity<?> getAllPaymentVoucher(@RequestParam String authToken) {
		PaymentVoucher[] paymentVoucherList = transService.getAllPaymentVoucher(authToken);
		return new ResponseEntity<>(paymentVoucherList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentVoucher.class, value = "Get a PaymentVoucher") // label for swagger
	@GetMapping("/operations/paymentvoucher/{paymentVoucherId}")
	public ResponseEntity<?> getPaymentVoucher(@PathVariable String paymentVoucherId, @RequestParam String authToken) {
		PaymentVoucher dbPaymentVoucher = transService.getPaymentVoucher(paymentVoucherId, authToken);
		return new ResponseEntity<>(dbPaymentVoucher, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentVoucher.class, value = "Create PaymentVoucher") // label for swagger
	@PostMapping("/operations/paymentvoucher")
	public ResponseEntity<?> postPaymentVoucher(@Valid @RequestBody AddPaymentVoucher newPaymentVoucher,
												@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		PaymentVoucher createdPaymentVoucher = transService.createPaymentVoucher(newPaymentVoucher, loginUserID, authToken);
		return new ResponseEntity<>(createdPaymentVoucher, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentVoucher.class, value = "Update PaymentVoucher") // label for swagger
	@PatchMapping("/operations/paymentvoucher/{paymentVoucher}")
	public ResponseEntity<?> patchPaymentVoucher(@PathVariable String paymentVoucher,
												 @RequestParam String loginUserID,
												 @Valid @RequestBody UpdatePaymentVoucher updatePaymentVoucher,
												 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PaymentVoucher updatedPaymentVoucher = transService.updatePaymentVoucher(paymentVoucher, loginUserID,
				updatePaymentVoucher, authToken);
		return new ResponseEntity<>(updatedPaymentVoucher, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentVoucher.class, value = "Delete PaymentVoucher") // label for swagger
	@DeleteMapping("/operations/paymentvoucher/{paymentVoucher}")
	public ResponseEntity<?> deletePaymentVoucher(@PathVariable String paymentVoucher, @RequestParam String loginUserID,
												  @RequestParam String authToken) {
		transService.deletePaymentVoucher(paymentVoucher, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = PaymentVoucher[].class, value = "Find PaymentVoucher") // label for swagger
	@PostMapping("/operations/paymentvoucher/find")
	public PaymentVoucher[] findPaymentVoucher(@RequestBody FindPaymentVoucher findPaymentVoucher,
											   @RequestParam String authToken)
			throws Exception {
		return transService.findPaymentVoucher(findPaymentVoucher, authToken);
	}
	//-----------------------------------Quote------------------------------------------------------------
	@ApiOperation(response = Quote.class, value = "Get all Quote details") // label for swagger
	@GetMapping("/crm/quote")
	public ResponseEntity<?> getAllQuote(@RequestParam String authToken) {
		Quote[] quoteList = transService.getAllQuote(authToken);
		return new ResponseEntity<>(quoteList, HttpStatus.OK);
	}

	@ApiOperation(response = Quote.class, value = "Get a Quote") // label for swagger
	@GetMapping("/crm/quote/{quoteId}")
	public ResponseEntity<?> getQuote(@PathVariable String quoteId,@RequestParam String authToken) {
		Quote dbQuote = transService.getQuote(quoteId, authToken);
		return new ResponseEntity<>(dbQuote, HttpStatus.OK);
	}

	@ApiOperation(response = Quote.class, value = "Create Quote") // label for swagger
	@PostMapping("/crm/quote")
	public ResponseEntity<?> postQuote(@Valid @RequestBody AddQuote newQuote,
									   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Quote createdQuote = transService.createQuote(newQuote, loginUserID, authToken);
		return new ResponseEntity<>(createdQuote, HttpStatus.OK);
	}

	@ApiOperation(response = Quote.class, value = "Update Quote") // label for swagger
	@PatchMapping("/crm/quote/{quote}")
	public ResponseEntity<?> patchQuote(@PathVariable String quote,
										@RequestParam String loginUserID,
										@Valid @RequestBody UpdateQuote updateQuote,
										@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Quote updatedQuote = transService.updateQuote(quote, loginUserID,
				updateQuote, authToken);
		return new ResponseEntity<>(updatedQuote, HttpStatus.OK);
	}

	@ApiOperation(response = Quote.class, value = "Delete Quote") // label for swagger
	@DeleteMapping("/crm/quote/{quote}")
	public ResponseEntity<?> deleteQuote(@PathVariable String quote, @RequestParam String loginUserID,
										 @RequestParam String authToken) {
		transService.deleteQuote(quote, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = Quote[].class, value = "Find Quote") // label for swagger
	@PostMapping("/crm/quote/find")
	public Quote[] findQuote(@RequestBody FindQuote findQuote,
							 @RequestParam String authToken)
			throws Exception {
		return transService.findQuote(findQuote, authToken);
	}
	//-----------------------------------StorageUnit------------------------------------------------------------
	@ApiOperation(response = StorageUnit.class, value = "Get all StorageUnit details") // label for swagger
	@GetMapping("/master/storageunit")
	public ResponseEntity<?> getAllStorageUnit(@RequestParam String authToken) {
		StorageUnit[] storageUnitList = transService.getAllStorageUnit(authToken);
		return new ResponseEntity<>(storageUnitList, HttpStatus.OK);
	}

	@ApiOperation(response = StorageUnit.class, value = "Get a StorageUnit") // label for swagger
	@GetMapping("/master/storageunit/{storageUnitId}")
	public ResponseEntity<?> getStorageUnit(@PathVariable String storageUnitId, @RequestParam String authToken) {
		StorageUnit dbStorageUnit = transService.getStorageUnit(storageUnitId, authToken);
		return new ResponseEntity<>(dbStorageUnit, HttpStatus.OK);
	}

	@ApiOperation(response = StorageUnit.class, value = "Create StorageUnit") // label for swagger
	@PostMapping("/master/storageunit")
	public ResponseEntity<?> postStorageUnit(@Valid @RequestBody AddStorageUnit newStorageUnit,
											 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		StorageUnit createdStorageUnit = transService.createStorageUnit(newStorageUnit, loginUserID, authToken);
		return new ResponseEntity<>(createdStorageUnit, HttpStatus.OK);
	}

	@ApiOperation(response = StorageUnit.class, value = "Update StorageUnit") // label for swagger
	@PatchMapping("/master/storageunit/{storageUnit}")
	public ResponseEntity<?> patchStorageUnit(@PathVariable String storageUnit,
											  @RequestParam String loginUserID,
											  @Valid @RequestBody UpdateStorageUnit updateStorageUnit,
											  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		StorageUnit updatedStorageUnit = transService.updateStorageUnit(storageUnit, loginUserID,
				updateStorageUnit, authToken);
		return new ResponseEntity<>(updatedStorageUnit, HttpStatus.OK);
	}

	@ApiOperation(response = StorageUnit.class, value = "Delete StorageUnit") // label for swagger
	@DeleteMapping("/master/storageunit/{storageUnit}")
	public ResponseEntity<?> deleteStorageUnit(@PathVariable String storageUnit, @RequestParam String loginUserID,
											   @RequestParam String authToken) {
		transService.deleteStorageUnit(storageUnit, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = StorageUnit[].class, value = "Find StorageUnit") // label for swagger
	@PostMapping("/master/storageunit/find")
	public StorageUnit[] findStorageUnit(@RequestBody FindStorageUnit findStorageUnit,
										 @RequestParam String authToken)
			throws Exception {
		return transService.findStorageUnit(findStorageUnit, authToken);
	}
	//-----------------------------------WorkOrder------------------------------------------------------------
	@ApiOperation(response = WorkOrder.class, value = "Get all WorkOrder details") // label for swagger
	@GetMapping("/operations/workorder")
	public ResponseEntity<?> getAllWorkOrder(@RequestParam String authToken) {
		WorkOrder[] workOrderList = transService.getAllWorkOrder(authToken);
		return new ResponseEntity<>(workOrderList, HttpStatus.OK);
	}

	@ApiOperation(response = GWorkOrder.class, value = "Get a WorkOrder") // label for swagger
	@GetMapping("/operations/workorder/{workOrderId}")
	public ResponseEntity<?> getWorkOrder(@PathVariable String workOrderId, @RequestParam String authToken) {
		GWorkOrder dbWorkOrder = transService.getWorkOrder(workOrderId, authToken);
		return new ResponseEntity<>(dbWorkOrder, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrder.class, value = "Create WorkOrder") // label for swagger
	@PostMapping("/operations/workorder")
	public ResponseEntity<?> postWorkOrder(@Valid @RequestBody AddWorkOrder newWorkOrder,
										   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		WorkOrder createdWorkOrder = transService.createWorkOrder(newWorkOrder, loginUserID, authToken);
		return new ResponseEntity<>(createdWorkOrder, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrder.class, value = "Update WorkOrder") // label for swagger
	@PatchMapping("/operations/workorder/{workOrder}")
	public ResponseEntity<?> patchWorkOrder(@PathVariable String workOrder,
											@RequestParam String loginUserID,
											@Valid @RequestBody UpdateWorkOrder updateWorkOrder,
											@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrder updatedWorkOrder = transService.updateWorkOrder(workOrder, loginUserID,
				updateWorkOrder, authToken);
		return new ResponseEntity<>(updatedWorkOrder, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrder.class, value = "Delete WorkOrder") // label for swagger
	@DeleteMapping("/operations/workorder/{workOrder}")
	public ResponseEntity<?> deleteWorkOrder(@PathVariable String workOrder, @RequestParam String loginUserID,
											 @RequestParam String authToken) {
		transService.deleteWorkOrder(workOrder, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = GWorkOrder[].class, value = "Find WorkOrder") // label for swagger
	@PostMapping("/operations/workorder/find")
	public GWorkOrder[] findWorkOrder(@RequestBody FindWorkOrder findWorkOrder,
									 @RequestParam String authToken)
			throws Exception {
		return transService.findWorkOrder(findWorkOrder, authToken);
	}
	//-----------------------------------FlRent------------------------------------------------------------
	@ApiOperation(response = FlRent.class, value = "Get all FlRent details") // label for swagger
	@GetMapping("/master/flrent")
	public ResponseEntity<?> getAllFlRent(@RequestParam String authToken) {
		FlRent[] flRentList = transService.getAllFlRent(authToken);
		return new ResponseEntity<>(flRentList, HttpStatus.OK);
	}

	@ApiOperation(response = FlRent.class, value = "Get a FlRent") // label for swagger
	@GetMapping("/master/flrent/{flRentId}")
	public ResponseEntity<?> getFlRent(@PathVariable String flRentId, @RequestParam String authToken) {
		FlRent dbFlRent = transService.getFlRent(flRentId, authToken);
		return new ResponseEntity<>(dbFlRent, HttpStatus.OK);
	}

	@ApiOperation(response = FlRent.class, value = "Create FlRent") // label for swagger
	@PostMapping("/master/flrent")
	public ResponseEntity<?> postFlRent(@Valid @RequestBody AddFlRent newFlRent,
										@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		FlRent createdFlRent = transService.createFlRent(newFlRent, loginUserID, authToken);
		return new ResponseEntity<>(createdFlRent, HttpStatus.OK);
	}

	@ApiOperation(response = FlRent.class, value = "Update FlRent") // label for swagger
	@PatchMapping("/master/flrent/{flRent}")
	public ResponseEntity<?> patchFlRent(@PathVariable String flRent,
										 @RequestParam String loginUserID,
										 @Valid @RequestBody UpdateFlRent updateFlRent,
										 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		FlRent updatedFlRent = transService.updateFlRent(flRent, loginUserID,
				updateFlRent, authToken);
		return new ResponseEntity<>(updatedFlRent, HttpStatus.OK);
	}

	@ApiOperation(response = FlRent.class, value = "Delete FlRent") // label for swagger
	@DeleteMapping("/master/flrent/{flRent}")
	public ResponseEntity<?> deleteFlRent(@PathVariable String flRent, @RequestParam String loginUserID,
										  @RequestParam String authToken) {
		transService.deleteFlRent(flRent, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = FlRent[].class, value = "Find FlRent") // label for swagger
	@PostMapping("/master/flrent/find")
	public FlRent[] findFlRent(@RequestBody FindFlRent findFlRent,
							   @RequestParam String authToken)
			throws Exception {
		return transService.findFlRent(findFlRent, authToken);
	}
	//-----------------------------------HandlingCharge------------------------------------------------------------
	@ApiOperation(response = HandlingCharge.class, value = "Get all HandlingCharge details") // label for swagger
	@GetMapping("/master/handlingcharge")
	public ResponseEntity<?> getAllHandlingCharge(@RequestParam String authToken) {
		HandlingCharge[] handlingChargeList = transService.getAllHandlingCharge(authToken);
		return new ResponseEntity<>(handlingChargeList, HttpStatus.OK);
	}

	@ApiOperation(response = HandlingCharge.class, value = "Get a HandlingCharge") // label for swagger
	@GetMapping("/master/handlingcharge/{handlingChargeId}")
	public ResponseEntity<?> getHandlingCharge(@PathVariable String handlingChargeId, @RequestParam String authToken) {
		HandlingCharge dbHandlingCharge = transService.getHandlingCharge(handlingChargeId, authToken);
		return new ResponseEntity<>(dbHandlingCharge, HttpStatus.OK);
	}

	@ApiOperation(response = HandlingCharge.class, value = "Create HandlingCharge") // label for swagger
	@PostMapping("/master/handlingcharge")
	public ResponseEntity<?> postHandlingCharge(@Valid @RequestBody AddHandlingCharge newHandlingCharge,
												@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		HandlingCharge createdHandlingCharge = transService.createHandlingCharge(newHandlingCharge, loginUserID, authToken);
		return new ResponseEntity<>(createdHandlingCharge, HttpStatus.OK);
	}

	@ApiOperation(response = HandlingCharge.class, value = "Update HandlingCharge") // label for swagger
	@PatchMapping("/master/handlingcharge/{handlingCharge}")
	public ResponseEntity<?> patchHandlingCharge(@PathVariable String handlingCharge,
												 @RequestParam String loginUserID,
												 @Valid @RequestBody UpdateHandlingCharge updateHandlingCharge,
												 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		HandlingCharge updatedHandlingCharge = transService.updateHandlingCharge(handlingCharge, loginUserID,
				updateHandlingCharge, authToken);
		return new ResponseEntity<>(updatedHandlingCharge, HttpStatus.OK);
	}

	@ApiOperation(response = HandlingCharge.class, value = "Delete HandlingCharge") // label for swagger
	@DeleteMapping("/master/handlingcharge/{handlingCharge}")
	public ResponseEntity<?> deleteHandlingCharge(@PathVariable String handlingCharge, @RequestParam String loginUserID,
												  @RequestParam String authToken) {
		transService.deleteHandlingCharge(handlingCharge, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = HandlingCharge[].class, value = "Find HandlingCharge") // label for swagger
	@PostMapping("/master/handlingcharge/find")
	public HandlingCharge[] findHandlingCharge(@RequestBody FindHandlingCharge findHandlingCharge,
											   @RequestParam String authToken)
			throws Exception {
		return transService.findHandlingCharge(findHandlingCharge, authToken);
	}
	//-----------------------------------Trip------------------------------------------------------------
	@ApiOperation(response = Trip.class, value = "Get all Trip details") // label for swagger
	@GetMapping("/master/trip")
	public ResponseEntity<?> getAllTrip(@RequestParam String authToken) {
		Trip[] tripList = transService.getAllTrip(authToken);
		return new ResponseEntity<>(tripList, HttpStatus.OK);
	}

	@ApiOperation(response = Trip.class, value = "Get a Trip") // label for swagger
	@GetMapping("/master/trip/{tripId}")
	public ResponseEntity<?> getTrip(@PathVariable String tripId, @RequestParam String authToken) {
		Trip dbTrip = transService.getTrip(tripId, authToken);
		return new ResponseEntity<>(dbTrip, HttpStatus.OK);
	}

	@ApiOperation(response = Trip.class, value = "Create Trip") // label for swagger
	@PostMapping("/master/trip")
	public ResponseEntity<?> postTrip(@Valid @RequestBody AddTrip newTrip,
									  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Trip createdTrip = transService.createTrip(newTrip, loginUserID, authToken);
		return new ResponseEntity<>(createdTrip, HttpStatus.OK);
	}

	@ApiOperation(response = Trip.class, value = "Update Trip") // label for swagger
	@PatchMapping("/master/trip/{trip}")
	public ResponseEntity<?> patchTrip(@PathVariable String trip,
									   @RequestParam String loginUserID,
									   @Valid @RequestBody UpdateTrip updateTrip,
									   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Trip updatedTrip = transService.updateTrip(trip, loginUserID,
				updateTrip, authToken);
		return new ResponseEntity<>(updatedTrip, HttpStatus.OK);
	}

	@ApiOperation(response = Trip.class, value = "Delete Trip") // label for swagger
	@DeleteMapping("/master/trip/{trip}")
	public ResponseEntity<?> deleteTrip(@PathVariable String trip, @RequestParam String loginUserID,
										@RequestParam String authToken) {
		transService.deleteTrip(trip, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Find
	@ApiOperation(response = Trip[].class, value = "Find Trip") // label for swagger
	@PostMapping("/master/trip/find")
	public Trip[] findTrip(@RequestBody FindTrip findTrip,
						   @RequestParam String authToken)
			throws Exception {
		return transService.findTrip(findTrip, authToken);
	}

	//-----------------------------------Report------------------------------------------------------------
	//workOrderStatusReport
	@ApiOperation(response = Optional.class, value = "Report_Work_Order_Status") // label for swagger
	@PostMapping("/reports/workOrderStatusReport")
	public ResponseEntity<?> getWorkOrderStatus (@RequestBody WorkOrderStatusModel workOrderStatus, @RequestParam String authToken) throws ParseException {
		WorkOrderStatusReport[] workOrderStatusData = transService.getWorkOrderStatus(workOrderStatus, authToken);
		return new ResponseEntity<>(workOrderStatusData, HttpStatus.OK);
	}
	//efficiencyRecordReport
	@ApiOperation(response = Optional.class, value = "Report_Efficiency_Record") // label for swagger
	@PostMapping("/reports/efficiencyRecordReport")
	public ResponseEntity<?> getEfficiencyRecord (@RequestBody EfficiencyRecord efficiencyRecord, @RequestParam String authToken)
			throws Exception {
		EfficiencyRecordReport[] data = transService.getEfficiencyRecord(efficiencyRecord,authToken);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	//quotationStatusReport
	@ApiOperation(response = Optional.class, value = "Report_Quotation_Status") // label for swagger
	@PostMapping("/reports/quotationStatusReport")
	public ResponseEntity<?> getQuotationStatus (@RequestBody QuotationStatus quotationStatus, @RequestParam String authToken)
			throws Exception {
		QuotationStatusReport[] data = transService.getQuotationStatus(quotationStatus,authToken);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	//enquiryStatusReport
	@ApiOperation(response = Optional.class, value = "Report_Enquiry_Status") // label for swagger
	@PostMapping("/reports/enquiryStatusReport")
	public ResponseEntity<?> getEnquiryStatus (@RequestBody EnquiryStatusModel enquiryStatus, @RequestParam String authToken)
			throws Exception {
		EnquiryStatusReport[] data = transService.getEnquiryStatus(enquiryStatus,authToken);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	//Fillrate Status report
	@ApiOperation(response = Optional.class, value = "Report_Fillrate_Status") // label for swagger
	@PostMapping("/reports/fillrateStatusReport")
	public ResponseEntity<?> getFillrateStatus (@RequestBody FillrateStatus fillrateStatus, @RequestParam String authToken)
			throws Exception {
		FillrateStatusReport[] data = transService.getFillrateStatus(fillrateStatus,authToken);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	//ContractRenewal Status report
	@ApiOperation(response = Optional.class, value = "Report_Contract_Renewal_Status") // label for swagger
	@PostMapping("/reports/contractRenewalStatusReport")
	public ResponseEntity<?> getContractRenewalStatus (@RequestBody ContractRenewalStatus contractRenewalStatus, @RequestParam String authToken)
			throws Exception {
		ContractRenewalStatusReport[] data = transService.getContractRenewalStatus(contractRenewalStatus,authToken);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	//Payment Due Status report
	@ApiOperation(response = Optional.class, value = "Report_Payment_Due_Status") // label for swagger
	@PostMapping("/reports/paymentDueStatusReport")
	public ResponseEntity<?> getPaymentDueStatus (@RequestBody PaymentDueStatus paymentDueStatus, @RequestParam String authToken)
			throws Exception {
		PaymentDueStatusReport[] data = transService.getPaymentDueStatus(paymentDueStatus,authToken);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	//Document Status report
	@ApiOperation(response = Optional.class, value = "Report_Document_Status") // label for swagger
	@PostMapping("/reports/documentStatusReport")
	public ResponseEntity<?> getDocumentStatus (@RequestBody DocumentStatusInput documentStatusInput, @RequestParam String authToken)
			throws Exception {
		DocumentStatusReport data = transService.getDocumentStatus(documentStatusInput,authToken);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	//---------------------------------Batch Upload-----------------------------------------------------------
	//agreement
	@ApiOperation(response = UploadFileResponse.class, value = "Upload Files") // label for swagger
	@PostMapping("/batchupload/agreement")
	public ResponseEntity<?> batchUploadAgreement(@RequestParam("file") MultipartFile file)
			throws Exception {
		Map<String, String> response = fileStorageService.storeFile(file);
		batchJobScheduler.runJobAgreement();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	//storageUnit
	@ApiOperation(response = UploadFileResponse.class, value = "Upload Files") // label for swagger
	@PostMapping("/batchupload/storageunit")
	public ResponseEntity<?> batchUploadStorageUnit(@RequestParam("file") MultipartFile file)
			throws Exception {
		Map<String, String> response = fileStorageService.storeFile(file);
		batchJobScheduler.runJobStorageUnit();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	//storageNumber
	@ApiOperation(response = UploadFileResponse.class, value = "Upload Files") // label for swagger
	@PostMapping("/batchupload/storenumber")
	public ResponseEntity<?> batchUploadStoreNumber(@RequestParam("file") MultipartFile file)
			throws Exception {
		Map<String, String> response = fileStorageService.storeFile(file);
		batchJobScheduler.runJobStoreNumber();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	//------------------------Customer Drop down-----------------------------------------------------------
	@ApiOperation(response = CustomerDropDown.class, value = "Get a Customer Dropdown") // label for swagger
	@GetMapping("/reports/dropdown/customerName")
	public ResponseEntity<?> getCustomerDropdownList(@RequestParam String authToken) {
		CustomerDropdownList customerDropdownList = transService.getCustomerDropdownList(authToken);
		return new ResponseEntity<>(customerDropdownList, HttpStatus.OK);
	}

	//------------------------Storage Drop down-----------------------------------------------------------
	@ApiOperation(response = StorageDropDown.class, value = "Get a Storage Dropdown") // label for swagger
	@GetMapping("/reports/dropdown/storageUnit")
	public ResponseEntity<?> getStorageDropdownList(@RequestParam String authToken) {
		StorageDropdownList storageUnitDropdownList = transService.getStorageDropdownList(authToken);
		return new ResponseEntity<>(storageUnitDropdownList, HttpStatus.OK);
	}

	//------------------------Customer Details-----------------------------------------------------------
	@ApiOperation(response = Dropdown.class, value = "Customer Details") // label for swagger
	@PostMapping("/reports/customerDetail")
	public ResponseEntity<?> getCustomerDetail(@Valid @RequestBody CustomerDetailInput customerDetailInput,
															 @RequestParam String authToken)
			throws Exception {
		Dropdown createdDetails = transService.createCustomerDetail(customerDetailInput, authToken);
		return new ResponseEntity<>(createdDetails , HttpStatus.OK);
	}
	//------------------------Dashboard Billed and Paid Amount-----------------------------------------------------------
	@ApiOperation(response = Optional.class, value = "Get Billed Paid Amount MonthWise") // label for swagger
	@GetMapping("/reports/dashboard/billedAndPaidAmount/{year}")
	public ResponseEntity<?> getBilledPaidAmount(@PathVariable Year year,@RequestParam String authToken) {
		BilledPaid billedPaid = transService.getBilledPaid(year,authToken);
		return new ResponseEntity<>(billedPaid, HttpStatus.OK);
	}
	//------------------------Dashboard Lead And Customer-----------------------------------------------------------
	@ApiOperation(response = Optional.class, value = "Get Lead and Customer Count MonthWise") // label for swagger
	@GetMapping("/reports/dashboard/leadAndCustomer/{year}")
	public ResponseEntity<?> getLeadAndCustomer(@PathVariable Year year,@RequestParam String authToken) {
		LeadAndCustomer leadAndCustomer = transService.getLeadAndCustomer(year,authToken);
		return new ResponseEntity<>(leadAndCustomer, HttpStatus.OK);
	}

}
