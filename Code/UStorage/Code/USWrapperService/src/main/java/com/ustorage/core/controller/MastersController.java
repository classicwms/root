package com.ustorage.core.controller;

import com.ustorage.core.batch.scheduler.BatchJobScheduler;
import com.ustorage.core.config.PropertiesConfig;
import com.ustorage.core.exception.BadRequestException;
import com.ustorage.core.model.auth.AuthToken;
import com.ustorage.core.model.auth.AuthTokenRequest;
import com.ustorage.core.model.masters.*;
import com.ustorage.core.service.AuthTokenService;
import com.ustorage.core.service.DocStorageService;
import com.ustorage.core.service.FileStorageService;
import com.ustorage.core.service.MastersService;
import com.ustorage.core.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Master Service" }, value = "AccountStatus Operations related to AccountStatusController")
@SwaggerDefinition(tags = { @Tag(name = "AccountStatus", description = "Operations related to AccountStatus") })
@RequestMapping("/us-master-service")
@RestController
public class MastersController {

	@Autowired
	MastersService mastersService;

	@Autowired
	FileStorageService fileStorageService;

	@Autowired
	DocStorageService docStorageService;

	@Autowired
	AuthTokenService authTokenService;

	@Autowired
	PropertiesConfig propertiesConfig;

	@Autowired
	BatchJobScheduler batchJobScheduler;

	//-----------------------------------auth token------------------------------------------------------------
	@ApiOperation(response = Optional.class, value = "OAuth Token") // label for swagger
	@PostMapping("/auth-token")
	public ResponseEntity<?> authToken(@Valid @RequestBody AuthTokenRequest authTokenRequest) {
		AuthToken authToken = authTokenService.getAuthToken(authTokenRequest);
		return new ResponseEntity<>(authToken, HttpStatus.OK);
	}

	//-----------------------------------Document Upload------------------------------------------------------------
	@ApiOperation(response = UploadFileResponse.class, value = "Document Storage Upload") // label for swagger
	@PostMapping("/doc-storage/upload")
	public ResponseEntity<?> docStorageUpload(@RequestParam String location, @RequestParam("file") MultipartFile file)
			throws Exception {
		if (location == null) {
			throw new BadRequestException("Location can't be blank. Please provide either Agreement or Document as Location");
		}
		Map<String, String> response = fileStorageService.storeFile(location, file);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	//-----------------------------------Document Download------------------------------------------------------------
	@ApiOperation(response = Optional.class, value = "Document Storage Download") // label for swagger
	@GetMapping("/doc-storage/download")
	public ResponseEntity<?> docStorageDownload(@RequestParam String location, @RequestParam String fileName)
			throws Exception {
		String filePath = docStorageService.getQualifiedFilePath (location, fileName);
		File file = new File (filePath);
		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		header.add("Pragma", "no-cache");
		header.add("Expires", "0");
		return ResponseEntity.ok()
				.headers(header)
				.contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}

	//-----------------------------------AccountStatus------------------------------------------------------------
	@ApiOperation(response = AccountStatus.class, value = "Get all AccountStatus details") // label for swagger
	@GetMapping("/accountstatus")
	public ResponseEntity<?> getAllAccountStatus(@RequestParam String authToken) {
		AccountStatus[] accountStatusList = mastersService.getAllAccountStatus(authToken);
		return new ResponseEntity<>(accountStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = AccountStatus.class, value = "Get a AccountStatus") // label for swagger
	@GetMapping("/accountstatus/{accountStatusId}")
	public ResponseEntity<?> getAccountStatus(@PathVariable String accountStatusId, @RequestParam String authToken) {
		AccountStatus dbAccountStatus = mastersService.getAccountStatus(accountStatusId, authToken);
		return new ResponseEntity<>(dbAccountStatus, HttpStatus.OK);
	}

	@ApiOperation(response = AccountStatus.class, value = "Create AccountStatus") // label for swagger
	@PostMapping("/accountstatus")
	public ResponseEntity<?> postAccountStatus(@Valid @RequestBody AddAccountStatus newAccountStatus,
			@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		AccountStatus createdAccountStatus = mastersService.createAccountStatus(newAccountStatus, loginUserID, authToken);
		return new ResponseEntity<>(createdAccountStatus, HttpStatus.OK);
	}

	@ApiOperation(response = AccountStatus.class, value = "Update AccountStatus") // label for swagger
	@PatchMapping("/accountstatus/{accountStatusId}")
	public ResponseEntity<?> patchAccountStatus(@PathVariable String accountStatusId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateAccountStatus updateAccountStatus,
			@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		AccountStatus updatedAccountStatus = mastersService.updateAccountStatus(accountStatusId, loginUserID,
				updateAccountStatus, authToken);
		return new ResponseEntity<>(updatedAccountStatus, HttpStatus.OK);
	}

	@ApiOperation(response = AccountStatus.class, value = "Delete AccountStatus") // label for swagger
	@DeleteMapping("/accountstatus/{accountStatusId}")
	public ResponseEntity<?> deleteAccountStatus(@PathVariable String accountStatusId, @RequestParam String loginUserID,
			@RequestParam String authToken) {
		mastersService.deleteAccountStatus(accountStatusId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------Bin------------------------------------------------------------
	@ApiOperation(response = Bin.class, value = "Get all Bin details") // label for swagger
	@GetMapping("/bin")
	public ResponseEntity<?> getAllBin(@RequestParam String authToken) {
		Bin[] binList = mastersService.getAllBin(authToken);
		return new ResponseEntity<>(binList, HttpStatus.OK);
	}

	@ApiOperation(response = Bin.class, value = "Get a Bin") // label for swagger
	@GetMapping("/bin/{binId}")
	public ResponseEntity<?> getBin(@PathVariable String binId,@RequestParam String authToken) {
		Bin dbBin = mastersService.getBin(binId, authToken);
		return new ResponseEntity<>(dbBin, HttpStatus.OK);
	}

	@ApiOperation(response = Bin.class, value = "Create Bin") // label for swagger
	@PostMapping("/bin")
	public ResponseEntity<?> postBin(@Valid @RequestBody AddBin newBin,
									 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Bin createdBin = mastersService.createBin(newBin, loginUserID, authToken);
		return new ResponseEntity<>(createdBin, HttpStatus.OK);
	}

	@ApiOperation(response = Bin.class, value = "Update Bin") // label for swagger
	@PatchMapping("/bin/{binId}")
	public ResponseEntity<?> patchBin(@PathVariable String binId,
									  @RequestParam String loginUserID,
									  @Valid @RequestBody UpdateBin updateBin,
									  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Bin updatedBin = mastersService.updateBin(binId, loginUserID,
				updateBin, authToken);
		return new ResponseEntity<>(updatedBin, HttpStatus.OK);
	}

	@ApiOperation(response = Bin.class, value = "Delete Bin") // label for swagger
	@DeleteMapping("/bin/{binId}")
	public ResponseEntity<?> deleteBin(@PathVariable String binId, @RequestParam String loginUserID,
									   @RequestParam String authToken) {
		mastersService.deleteBin(binId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------BusinessPartnerType------------------------------------------------------------
	@ApiOperation(response = BusinessPartnerType.class, value = "Get all BusinessPartnerType details") // label for swagger
	@GetMapping("/businesspartnertype")
	public ResponseEntity<?> getAllBusinessPartnerType(@RequestParam String authToken) {
		BusinessPartnerType[] businessPartnerTypeList = mastersService.getAllBusinessPartnerType(authToken);
		return new ResponseEntity<>(businessPartnerTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = BusinessPartnerType.class, value = "Get a BusinessPartnerType") // label for swagger
	@GetMapping("/businesspartnertype/{businessPartnerTypeId}")
	public ResponseEntity<?> getBusinessPartnerType(@PathVariable String businessPartnerTypeId, @RequestParam String authToken) {
		BusinessPartnerType dbBusinessPartnerType = mastersService.getBusinessPartnerType(businessPartnerTypeId, authToken);
		return new ResponseEntity<>(dbBusinessPartnerType, HttpStatus.OK);
	}

	@ApiOperation(response = BusinessPartnerType.class, value = "Create BusinessPartnerType") // label for swagger
	@PostMapping("/businesspartnertype")
	public ResponseEntity<?> postBusinessPartnerType(@Valid @RequestBody AddBusinessPartnerType newBusinessPartnerType,
													 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		BusinessPartnerType createdBusinessPartnerType = mastersService.createBusinessPartnerType(newBusinessPartnerType, loginUserID, authToken);
		return new ResponseEntity<>(createdBusinessPartnerType, HttpStatus.OK);
	}

	@ApiOperation(response = BusinessPartnerType.class, value = "Update BusinessPartnerType") // label for swagger
	@PatchMapping("/businesspartnertype/{businessPartnerType}")
	public ResponseEntity<?> patchBusinessPartnerType(@PathVariable String businessPartnerType,
													  @RequestParam String loginUserID,
													  @Valid @RequestBody UpdateBusinessPartnerType updateBusinessPartnerType,
													  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		BusinessPartnerType updatedBusinessPartnerType = mastersService.updateBusinessPartnerType(businessPartnerType, loginUserID,
				updateBusinessPartnerType, authToken);
		return new ResponseEntity<>(updatedBusinessPartnerType, HttpStatus.OK);
	}

	@ApiOperation(response = BusinessPartnerType.class, value = "Delete BusinessPartnerType") // label for swagger
	@DeleteMapping("/businesspartnertype/{businessPartnerType}")
	public ResponseEntity<?> deleteBusinessPartnerType(@PathVariable String businessPartnerType, @RequestParam String loginUserID,
													   @RequestParam String authToken) {
		mastersService.deleteBusinessPartnerType(businessPartnerType, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Currency------------------------------------------------------------
	@ApiOperation(response = Currency.class, value = "Get all Currency details") // label for swagger
	@GetMapping("/currency")
	public ResponseEntity<?> getAllCurrency(@RequestParam String authToken) {
		Currency[] currencyList = mastersService.getAllCurrency(authToken);
		return new ResponseEntity<>(currencyList, HttpStatus.OK);
	}

	@ApiOperation(response = Currency.class, value = "Get a Currency") // label for swagger
	@GetMapping("/currency/{currencyId}")
	public ResponseEntity<?> getCurrency(@PathVariable String currencyId,@RequestParam String authToken) {
		Currency dbCurrency = mastersService.getCurrency(currencyId, authToken);
		return new ResponseEntity<>(dbCurrency, HttpStatus.OK);
	}

	@ApiOperation(response = Currency.class, value = "Create Currency") // label for swagger
	@PostMapping("/currency")
	public ResponseEntity<?> postCurrency(@Valid @RequestBody AddCurrency newCurrency,
										  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Currency createdCurrency = mastersService.createCurrency(newCurrency, loginUserID, authToken);
		return new ResponseEntity<>(createdCurrency, HttpStatus.OK);
	}

	@ApiOperation(response = Currency.class, value = "Update Currency") // label for swagger
	@PatchMapping("/currency/{currencyId}")
	public ResponseEntity<?> patchCurrency(@PathVariable String currencyId,
										   @RequestParam String loginUserID,
										   @Valid @RequestBody UpdateCurrency updateCurrency,
										   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Currency updatedCurrency = mastersService.updateCurrency(currencyId, loginUserID,
				updateCurrency, authToken);
		return new ResponseEntity<>(updatedCurrency, HttpStatus.OK);
	}

	@ApiOperation(response = Currency.class, value = "Delete Currency") // label for swagger
	@DeleteMapping("/currency/{currencyId}")
	public ResponseEntity<?> deleteCurrency(@PathVariable String currencyId, @RequestParam String loginUserID,
											@RequestParam String authToken) {
		mastersService.deleteCurrency(currencyId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//-----------------------------------CustomerGroup------------------------------------------------------------
	@ApiOperation(response = CustomerGroup.class, value = "Get all CustomerGroup details") // label for swagger
	@GetMapping("/customergroup")
	public ResponseEntity<?> getAllCustomerGroup(@RequestParam String authToken) {
		CustomerGroup[] customerGroupList = mastersService.getAllCustomerGroup(authToken);
		return new ResponseEntity<>(customerGroupList, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerGroup.class, value = "Get a CustomerGroup") // label for swagger
	@GetMapping("/customergroup/{customerGroupId}")
	public ResponseEntity<?> getCustomerGroup(@PathVariable String customerGroupId, @RequestParam String authToken) {
		CustomerGroup dbCustomerGroup = mastersService.getCustomerGroup(customerGroupId, authToken);
		return new ResponseEntity<>(dbCustomerGroup, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerGroup.class, value = "Create CustomerGroup") // label for swagger
	@PostMapping("/customergroup")
	public ResponseEntity<?> postCustomerGroup(@Valid @RequestBody AddCustomerGroup newCustomerGroup,
											   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		CustomerGroup createdCustomerGroup = mastersService.createCustomerGroup(newCustomerGroup, loginUserID, authToken);
		return new ResponseEntity<>(createdCustomerGroup, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerGroup.class, value = "Update CustomerGroup") // label for swagger
	@PatchMapping("/customergroup/{customerGroupId}")
	public ResponseEntity<?> patchCustomerGroup(@PathVariable String customerGroupId,
												@RequestParam String loginUserID,
												@Valid @RequestBody UpdateCustomerGroup updateCustomerGroup,
												@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		CustomerGroup updatedCustomerGroup = mastersService.updateCustomerGroup(customerGroupId, loginUserID,
				updateCustomerGroup, authToken);
		return new ResponseEntity<>(updatedCustomerGroup, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerGroup.class, value = "Delete CustomerGroup") // label for swagger
	@DeleteMapping("/customergroup/{customerGroupId}")
	public ResponseEntity<?> deleteCustomerGroup(@PathVariable String customerGroupId, @RequestParam String loginUserID,
												 @RequestParam String authToken) {
		mastersService.deleteCustomerGroup(customerGroupId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//-----------------------------------CustomerType------------------------------------------------------------
	@ApiOperation(response = CustomerType.class, value = "Get all CustomerType details") // label for swagger
	@GetMapping("/customertype")
	public ResponseEntity<?> getAllCustomerType(@RequestParam String authToken) {
		CustomerType[] customerTypeList = mastersService.getAllCustomerType(authToken);
		return new ResponseEntity<>(customerTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerType.class, value = "Get a CustomerType") // label for swagger
	@GetMapping("/customertype/{customerTypeId}")
	public ResponseEntity<?> getCustomerType(@PathVariable String customerTypeId, @RequestParam String authToken) {
		CustomerType dbCustomerType = mastersService.getCustomerType(customerTypeId, authToken);
		return new ResponseEntity<>(dbCustomerType, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerType.class, value = "Create CustomerType") // label for swagger
	@PostMapping("/customertype")
	public ResponseEntity<?> postCustomerType(@Valid @RequestBody AddCustomerType newCustomerType,
											  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		CustomerType createdCustomerType = mastersService.createCustomerType(newCustomerType, loginUserID, authToken);
		return new ResponseEntity<>(createdCustomerType, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerType.class, value = "Update CustomerType") // label for swagger
	@PatchMapping("/customertype/{customerTypeId}")
	public ResponseEntity<?> patchCustomerType(@PathVariable String customerTypeId,
											   @RequestParam String loginUserID,
											   @Valid @RequestBody UpdateCustomerType updateCustomerType,
											   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		CustomerType updatedCustomerType = mastersService.updateCustomerType(customerTypeId, loginUserID,
				updateCustomerType, authToken);
		return new ResponseEntity<>(updatedCustomerType, HttpStatus.OK);
	}

	@ApiOperation(response = CustomerType.class, value = "Delete CustomerType") // label for swagger
	@DeleteMapping("/customertype/{customerTypeId}")
	public ResponseEntity<?> deleteCustomerType(@PathVariable String customerTypeId, @RequestParam String loginUserID,
												@RequestParam String authToken) {
		mastersService.deleteCustomerType(customerTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//-----------------------------------DocumentStatus------------------------------------------------------------
	@ApiOperation(response = DocumentStatus.class, value = "Get all DocumentStatus details") // label for swagger
	@GetMapping("/documentstatus")
	public ResponseEntity<?> getAllDocumentStatus(@RequestParam String authToken) {
		DocumentStatus[] documentStatusList = mastersService.getAllDocumentStatus(authToken);
		return new ResponseEntity<>(documentStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStatus.class, value = "Get a DocumentStatus") // label for swagger
	@GetMapping("/documentstatus/{documentStatusId}")
	public ResponseEntity<?> getDocumentStatus(@PathVariable String documentStatusId, @RequestParam String authToken) {
		DocumentStatus dbDocumentStatus = mastersService.getDocumentStatus(documentStatusId, authToken);
		return new ResponseEntity<>(dbDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStatus.class, value = "Create DocumentStatus") // label for swagger
	@PostMapping("/documentstatus")
	public ResponseEntity<?> postDocumentStatus(@Valid @RequestBody AddDocumentStatus newDocumentStatus,
												@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		DocumentStatus createdDocumentStatus = mastersService.createDocumentStatus(newDocumentStatus, loginUserID, authToken);
		return new ResponseEntity<>(createdDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStatus.class, value = "Update DocumentStatus") // label for swagger
	@PatchMapping("/documentstatus/{documentStatusId}")
	public ResponseEntity<?> patchDocumentStatus(@PathVariable String documentStatusId,
												 @RequestParam String loginUserID,
												 @Valid @RequestBody UpdateDocumentStatus updateDocumentStatus,
												 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		DocumentStatus updatedDocumentStatus = mastersService.updateDocumentStatus(documentStatusId, loginUserID,
				updateDocumentStatus, authToken);
		return new ResponseEntity<>(updatedDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStatus.class, value = "Delete DocumentStatus") // label for swagger
	@DeleteMapping("/documentstatus/{documentStatusId}")
	public ResponseEntity<?> deleteDocumentStatus(@PathVariable String documentStatusId, @RequestParam String loginUserID,
												  @RequestParam String authToken) {
		mastersService.deleteDocumentStatus(documentStatusId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//-----------------------------------DoorType------------------------------------------------------------
	@ApiOperation(response = DoorType.class, value = "Get all DoorType details") // label for swagger
	@GetMapping("/doortype")
	public ResponseEntity<?> getAllDoorType(@RequestParam String authToken) {
		DoorType[] doorTypeList = mastersService.getAllDoorType(authToken);
		return new ResponseEntity<>(doorTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = DoorType.class, value = "Get a DoorType") // label for swagger
	@GetMapping("/doortype/{doorTypeId}")
	public ResponseEntity<?> getDoorType(@PathVariable String doorTypeId, @RequestParam String authToken) {
		DoorType dbDoorType = mastersService.getDoorType(doorTypeId, authToken);
		return new ResponseEntity<>(dbDoorType, HttpStatus.OK);
	}

	@ApiOperation(response = DoorType.class, value = "Create DoorType") // label for swagger
	@PostMapping("/doortype")
	public ResponseEntity<?> postDoorType(@Valid @RequestBody AddDoorType newDoorType,
										  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		DoorType createdDoorType = mastersService.createDoorType(newDoorType, loginUserID, authToken);
		return new ResponseEntity<>(createdDoorType, HttpStatus.OK);
	}

	@ApiOperation(response = DoorType.class, value = "Update DoorType") // label for swagger
	@PatchMapping("/doortype/{doorTypeId}")
	public ResponseEntity<?> patchDoorType(@PathVariable String doorTypeId,
										   @RequestParam String loginUserID,
										   @Valid @RequestBody UpdateDoorType updateDoorType,
										   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		DoorType updatedDoorType = mastersService.updateDoorType(doorTypeId, loginUserID,
				updateDoorType, authToken);
		return new ResponseEntity<>(updatedDoorType, HttpStatus.OK);
	}

	@ApiOperation(response = DoorType.class, value = "Delete DoorType") // label for swagger
	@DeleteMapping("/doortype/{doorTypeId}")
	public ResponseEntity<?> deleteDoorType(@PathVariable String doorTypeId, @RequestParam String loginUserID,
											@RequestParam String authToken) {
		mastersService.deleteDoorType(doorTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//-----------------------------------EnquiryStatus------------------------------------------------------------
	@ApiOperation(response = EnquiryStatus.class, value = "Get all EnquiryStatus details") // label for swagger
	@GetMapping("/enquirystatus")
	public ResponseEntity<?> getAllEnquiryStatus(@RequestParam String authToken) {
		EnquiryStatus[] enquiryStatusList = mastersService.getAllEnquiryStatus(authToken);
		return new ResponseEntity<>(enquiryStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = EnquiryStatus.class, value = "Get a EnquiryStatus") // label for swagger
	@GetMapping("/enquirystatus/{enquiryStatusId}")
	public ResponseEntity<?> getEnquiryStatus(@PathVariable String enquiryStatusId, @RequestParam String authToken) {
		EnquiryStatus dbEnquiryStatus = mastersService.getEnquiryStatus(enquiryStatusId, authToken);
		return new ResponseEntity<>(dbEnquiryStatus, HttpStatus.OK);
	}

	@ApiOperation(response = EnquiryStatus.class, value = "Create EnquiryStatus") // label for swagger
	@PostMapping("/enquirystatus")
	public ResponseEntity<?> postEnquiryStatus(@Valid @RequestBody AddEnquiryStatus newEnquiryStatus,
											   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		EnquiryStatus createdEnquiryStatus = mastersService.createEnquiryStatus(newEnquiryStatus, loginUserID, authToken);
		return new ResponseEntity<>(createdEnquiryStatus, HttpStatus.OK);
	}

	@ApiOperation(response = EnquiryStatus.class, value = "Update EnquiryStatus") // label for swagger
	@PatchMapping("/enquirystatus/{enquiryStatusId}")
	public ResponseEntity<?> patchEnquiryStatus(@PathVariable String enquiryStatusId,
												@RequestParam String loginUserID,
												@Valid @RequestBody UpdateEnquiryStatus updateEnquiryStatus,
												@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		EnquiryStatus updatedEnquiryStatus = mastersService.updateEnquiryStatus(enquiryStatusId, loginUserID,
				updateEnquiryStatus, authToken);
		return new ResponseEntity<>(updatedEnquiryStatus, HttpStatus.OK);
	}

	@ApiOperation(response = EnquiryStatus.class, value = "Delete EnquiryStatus") // label for swagger
	@DeleteMapping("/enquirystatus/{enquiryStatusId}")
	public ResponseEntity<?> deleteEnquiryStatus(@PathVariable String enquiryStatusId, @RequestParam String loginUserID,
												 @RequestParam String authToken) {
		mastersService.deleteEnquiryStatus(enquiryStatusId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//-----------------------------------InvoiceCurrency------------------------------------------------------------
	@ApiOperation(response = InvoiceCurrency.class, value = "Get all InvoiceCurrency details") // label for swagger
	@GetMapping("/invoicecurrency")
	public ResponseEntity<?> getAllInvoiceCurrency(@RequestParam String authToken) {
		InvoiceCurrency[] invoiceCurrencyList = mastersService.getAllInvoiceCurrency(authToken);
		return new ResponseEntity<>(invoiceCurrencyList, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceCurrency.class, value = "Get a InvoiceCurrency") // label for swagger
	@GetMapping("/invoicecurrency/{invoiceCurrencyId}")
	public ResponseEntity<?> getInvoiceCurrency(@PathVariable String invoiceCurrencyId, @RequestParam String authToken) {
		InvoiceCurrency dbInvoiceCurrency = mastersService.getInvoiceCurrency(invoiceCurrencyId, authToken);
		return new ResponseEntity<>(dbInvoiceCurrency, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceCurrency.class, value = "Create InvoiceCurrency") // label for swagger
	@PostMapping("/invoicecurrency")
	public ResponseEntity<?> postInvoiceCurrency(@Valid @RequestBody AddInvoiceCurrency newInvoiceCurrency,
												 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		InvoiceCurrency createdInvoiceCurrency = mastersService.createInvoiceCurrency(newInvoiceCurrency, loginUserID, authToken);
		return new ResponseEntity<>(createdInvoiceCurrency, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceCurrency.class, value = "Update InvoiceCurrency") // label for swagger
	@PatchMapping("/invoicecurrency/{invoiceCurrencyId}")
	public ResponseEntity<?> patchInvoiceCurrency(@PathVariable String invoiceCurrencyId,
												  @RequestParam String loginUserID,
												  @Valid @RequestBody UpdateInvoiceCurrency updateInvoiceCurrency,
												  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		InvoiceCurrency updatedInvoiceCurrency = mastersService.updateInvoiceCurrency(invoiceCurrencyId, loginUserID,
				updateInvoiceCurrency, authToken);
		return new ResponseEntity<>(updatedInvoiceCurrency, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceCurrency.class, value = "Delete InvoiceCurrency") // label for swagger
	@DeleteMapping("/invoicecurrency/{invoiceCurrencyId}")
	public ResponseEntity<?> deleteInvoiceCurrency(@PathVariable String invoiceCurrencyId, @RequestParam String loginUserID,
												   @RequestParam String authToken) {
		mastersService.deleteInvoiceCurrency(invoiceCurrencyId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------InvoiceDocumentStatus------------------------------------------------------------
	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Get all InvoiceDocumentStatus details") // label for swagger
	@GetMapping("/invoicedocumentstatus")
	public ResponseEntity<?> getAllInvoiceDocumentStatus(@RequestParam String authToken) {
		InvoiceDocumentStatus[] invoiceDocumentStatusList = mastersService.getAllInvoiceDocumentStatus(authToken);
		return new ResponseEntity<>(invoiceDocumentStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Get a InvoiceDocumentStatus") // label for swagger
	@GetMapping("/invoicedocumentstatus/{invoiceDocumentStatusId}")
	public ResponseEntity<?> getInvoiceDocumentStatus(@PathVariable String invoiceDocumentStatusId, @RequestParam String authToken) {
		InvoiceDocumentStatus dbInvoiceDocumentStatus = mastersService.getInvoiceDocumentStatus(invoiceDocumentStatusId, authToken);
		return new ResponseEntity<>(dbInvoiceDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Create InvoiceDocumentStatus") // label for swagger
	@PostMapping("/invoicedocumentstatus")
	public ResponseEntity<?> postInvoiceDocumentStatus(@Valid @RequestBody AddInvoiceDocumentStatus newInvoiceDocumentStatus,
													   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		InvoiceDocumentStatus createdInvoiceDocumentStatus = mastersService.createInvoiceDocumentStatus(newInvoiceDocumentStatus, loginUserID, authToken);
		return new ResponseEntity<>(createdInvoiceDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Update InvoiceDocumentStatus") // label for swagger
	@PatchMapping("/invoicedocumentstatus/{invoiceDocumentStatusId}")
	public ResponseEntity<?> patchInvoiceDocumentStatus(@PathVariable String invoiceDocumentStatusId,
														@RequestParam String loginUserID,
														@Valid @RequestBody UpdateInvoiceDocumentStatus updateInvoiceDocumentStatus,
														@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		InvoiceDocumentStatus updatedInvoiceDocumentStatus = mastersService.updateInvoiceDocumentStatus(invoiceDocumentStatusId, loginUserID,
				updateInvoiceDocumentStatus, authToken);
		return new ResponseEntity<>(updatedInvoiceDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = InvoiceDocumentStatus.class, value = "Delete InvoiceDocumentStatus") // label for swagger
	@DeleteMapping("/invoicedocumentstatus/{invoiceDocumentStatusId}")
	public ResponseEntity<?> deleteInvoiceDocumentStatus(@PathVariable String invoiceDocumentStatusId, @RequestParam String loginUserID,
														 @RequestParam String authToken) {
		mastersService.deleteInvoiceDocumentStatus(invoiceDocumentStatusId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------ItemGroup------------------------------------------------------------
	@ApiOperation(response = ItemGroup.class, value = "Get all ItemGroup details") // label for swagger
	@GetMapping("/itemgroup")
	public ResponseEntity<?> getAllItemGroup(@RequestParam String authToken) {
		ItemGroup[] itemGroupList = mastersService.getAllItemGroup(authToken);
		return new ResponseEntity<>(itemGroupList, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroup.class, value = "Get a ItemGroup") // label for swagger
	@GetMapping("/itemgroup/{itemGroupId}")
	public ResponseEntity<?> getItemGroup(@PathVariable String itemGroupId, @RequestParam String authToken) {
		ItemGroup dbItemGroup = mastersService.getItemGroup(itemGroupId, authToken);
		return new ResponseEntity<>(dbItemGroup, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroup.class, value = "Create ItemGroup") // label for swagger
	@PostMapping("/itemgroup")
	public ResponseEntity<?> postItemGroup(@Valid @RequestBody AddItemGroup newItemGroup,
										   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		ItemGroup createdItemGroup = mastersService.createItemGroup(newItemGroup, loginUserID, authToken);
		return new ResponseEntity<>(createdItemGroup, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroup.class, value = "Update ItemGroup") // label for swagger
	@PatchMapping("/itemgroup/{itemGroupId}")
	public ResponseEntity<?> patchItemGroup(@PathVariable String itemGroupId,
											@RequestParam String loginUserID,
											@Valid @RequestBody UpdateItemGroup updateItemGroup,
											@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ItemGroup updatedItemGroup = mastersService.updateItemGroup(itemGroupId, loginUserID,
				updateItemGroup, authToken);
		return new ResponseEntity<>(updatedItemGroup, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroup.class, value = "Delete ItemGroup") // label for swagger
	@DeleteMapping("/itemgroup/{itemGroupId}")
	public ResponseEntity<?> deleteItemGroup(@PathVariable String itemGroupId, @RequestParam String loginUserID,
											 @RequestParam String authToken) {
		mastersService.deleteItemGroup(itemGroupId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------ItemType------------------------------------------------------------
	@ApiOperation(response = ItemType.class, value = "Get all ItemType details") // label for swagger
	@GetMapping("/itemtype")
	public ResponseEntity<?> getAllItemType(@RequestParam String authToken) {
		ItemType[] itemTypeList = mastersService.getAllItemType(authToken);
		return new ResponseEntity<>(itemTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = ItemType.class, value = "Get a ItemType") // label for swagger
	@GetMapping("/itemtype/{itemTypeId}")
	public ResponseEntity<?> getItemType(@PathVariable String itemTypeId, @RequestParam String authToken) {
		ItemType dbItemType = mastersService.getItemType(itemTypeId, authToken);
		return new ResponseEntity<>(dbItemType, HttpStatus.OK);
	}

	@ApiOperation(response = ItemType.class, value = "Create ItemType") // label for swagger
	@PostMapping("/itemtype")
	public ResponseEntity<?> postItemType(@Valid @RequestBody AddItemType newItemType,
										  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		ItemType createdItemType = mastersService.createItemType(newItemType, loginUserID, authToken);
		return new ResponseEntity<>(createdItemType, HttpStatus.OK);
	}

	@ApiOperation(response = ItemType.class, value = "Update ItemType") // label for swagger
	@PatchMapping("/itemtype/{itemTypeId}")
	public ResponseEntity<?> patchItemType(@PathVariable String itemTypeId,
										   @RequestParam String loginUserID,
										   @Valid @RequestBody UpdateItemType updateItemType,
										   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ItemType updatedItemType = mastersService.updateItemType(itemTypeId, loginUserID,
				updateItemType, authToken);
		return new ResponseEntity<>(updatedItemType, HttpStatus.OK);
	}

	@ApiOperation(response = ItemType.class, value = "Delete ItemType") // label for swagger
	@DeleteMapping("/itemtype/{itemTypeId}")
	public ResponseEntity<?> deleteItemType(@PathVariable String itemTypeId, @RequestParam String loginUserID,
											@RequestParam String authToken) {
		mastersService.deleteItemType(itemTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------ModeOfPayment------------------------------------------------------------
	@ApiOperation(response = ModeOfPayment.class, value = "Get all ModeOfPayment details") // label for swagger
	@GetMapping("/modeofpayment")
	public ResponseEntity<?> getAllModeOfPayment(@RequestParam String authToken) {
		ModeOfPayment[] modeOfPaymentList = mastersService.getAllModeOfPayment(authToken);
		return new ResponseEntity<>(modeOfPaymentList, HttpStatus.OK);
	}

	@ApiOperation(response = ModeOfPayment.class, value = "Get a ModeOfPayment") // label for swagger
	@GetMapping("/modeofpayment/{modeOfPaymentId}")
	public ResponseEntity<?> getModeOfPayment(@PathVariable String modeOfPaymentId, @RequestParam String authToken) {
		ModeOfPayment dbModeOfPayment = mastersService.getModeOfPayment(modeOfPaymentId, authToken);
		return new ResponseEntity<>(dbModeOfPayment, HttpStatus.OK);
	}

	@ApiOperation(response = ModeOfPayment.class, value = "Create ModeOfPayment") // label for swagger
	@PostMapping("/modeofpayment")
	public ResponseEntity<?> postModeOfPayment(@Valid @RequestBody AddModeOfPayment newModeOfPayment,
											   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		ModeOfPayment createdModeOfPayment = mastersService.createModeOfPayment(newModeOfPayment, loginUserID, authToken);
		return new ResponseEntity<>(createdModeOfPayment, HttpStatus.OK);
	}

	@ApiOperation(response = ModeOfPayment.class, value = "Update ModeOfPayment") // label for swagger
	@PatchMapping("/modeofpayment/{modeOfPaymentId}")
	public ResponseEntity<?> patchModeOfPayment(@PathVariable String modeOfPaymentId,
												@RequestParam String loginUserID,
												@Valid @RequestBody UpdateModeOfPayment updateModeOfPayment,
												@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ModeOfPayment updatedModeOfPayment = mastersService.updateModeOfPayment(modeOfPaymentId, loginUserID,
				updateModeOfPayment, authToken);
		return new ResponseEntity<>(updatedModeOfPayment, HttpStatus.OK);
	}

	@ApiOperation(response = ModeOfPayment.class, value = "Delete ModeOfPayment") // label for swagger
	@DeleteMapping("/modeofpayment/{modeOfPaymentId}")
	public ResponseEntity<?> deleteModeOfPayment(@PathVariable String modeOfPaymentId, @RequestParam String loginUserID,
												 @RequestParam String authToken) {
		mastersService.deleteModeOfPayment(modeOfPaymentId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Nationality------------------------------------------------------------
	@ApiOperation(response = Nationality.class, value = "Get all Nationality details") // label for swagger
	@GetMapping("/nationality")
	public ResponseEntity<?> getAllNationality(@RequestParam String authToken) {
		Nationality[] nationalityList = mastersService.getAllNationality(authToken);
		return new ResponseEntity<>(nationalityList, HttpStatus.OK);
	}

	@ApiOperation(response = Nationality.class, value = "Get a Nationality") // label for swagger
	@GetMapping("/nationality/{nationalityId}")
	public ResponseEntity<?> getNationality(@PathVariable String nationalityId, @RequestParam String authToken) {
		Nationality dbNationality = mastersService.getNationality(nationalityId, authToken);
		return new ResponseEntity<>(dbNationality, HttpStatus.OK);
	}

	@ApiOperation(response = Nationality.class, value = "Create Nationality") // label for swagger
	@PostMapping("/nationality")
	public ResponseEntity<?> postNationality(@Valid @RequestBody AddNationality newNationality,
											 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Nationality createdNationality = mastersService.createNationality(newNationality, loginUserID, authToken);
		return new ResponseEntity<>(createdNationality, HttpStatus.OK);
	}

	@ApiOperation(response = Nationality.class, value = "Update Nationality") // label for swagger
	@PatchMapping("/nationality/{nationalityId}")
	public ResponseEntity<?> patchNationality(@PathVariable String nationalityId,
											  @RequestParam String loginUserID,
											  @Valid @RequestBody UpdateNationality updateNationality,
											  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Nationality updatedNationality = mastersService.updateNationality(nationalityId, loginUserID,
				updateNationality, authToken);
		return new ResponseEntity<>(updatedNationality, HttpStatus.OK);
	}

	@ApiOperation(response = Nationality.class, value = "Delete Nationality") // label for swagger
	@DeleteMapping("/nationality/{nationalityId}")
	public ResponseEntity<?> deleteNationality(@PathVariable String nationalityId, @RequestParam String loginUserID,
											   @RequestParam String authToken) {
		mastersService.deleteNationality(nationalityId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------PaymentMode------------------------------------------------------------
	@ApiOperation(response = PaymentMode.class, value = "Get all PaymentMode details") // label for swagger
	@GetMapping("/paymentmode")
	public ResponseEntity<?> getAllPaymentMode(@RequestParam String authToken) {
		PaymentMode[] paymentModeList = mastersService.getAllPaymentMode(authToken);
		return new ResponseEntity<>(paymentModeList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentMode.class, value = "Get a PaymentMode") // label for swagger
	@GetMapping("/paymentmode/{paymentModeId}")
	public ResponseEntity<?> getPaymentMode(@PathVariable String paymentModeId, @RequestParam String authToken) {
		PaymentMode dbPaymentMode = mastersService.getPaymentMode(paymentModeId, authToken);
		return new ResponseEntity<>(dbPaymentMode, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentMode.class, value = "Create PaymentMode") // label for swagger
	@PostMapping("/paymentmode")
	public ResponseEntity<?> postPaymentMode(@Valid @RequestBody AddPaymentMode newPaymentMode,
											 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		PaymentMode createdPaymentMode = mastersService.createPaymentMode(newPaymentMode, loginUserID, authToken);
		return new ResponseEntity<>(createdPaymentMode, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentMode.class, value = "Update PaymentMode") // label for swagger
	@PatchMapping("/paymentmode/{paymentModeId}")
	public ResponseEntity<?> patchPaymentMode(@PathVariable String paymentModeId,
											  @RequestParam String loginUserID,
											  @Valid @RequestBody UpdatePaymentMode updatePaymentMode,
											  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PaymentMode updatedPaymentMode = mastersService.updatePaymentMode(paymentModeId, loginUserID,
				updatePaymentMode, authToken);
		return new ResponseEntity<>(updatedPaymentMode, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentMode.class, value = "Delete PaymentMode") // label for swagger
	@DeleteMapping("/paymentmode/{paymentModeId}")
	public ResponseEntity<?> deletePaymentMode(@PathVariable String paymentModeId, @RequestParam String loginUserID,
											   @RequestParam String authToken) {
		mastersService.deletePaymentMode(paymentModeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------PaymentPeriod------------------------------------------------------------
	@ApiOperation(response = PaymentPeriod.class, value = "Get all PaymentPeriod details") // label for swagger
	@GetMapping("/paymentperiod")
	public ResponseEntity<?> getAllPaymentPeriod(@RequestParam String authToken) {
		PaymentPeriod[] paymentPeriodList = mastersService.getAllPaymentPeriod(authToken);
		return new ResponseEntity<>(paymentPeriodList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentPeriod.class, value = "Get a PaymentPeriod") // label for swagger
	@GetMapping("/paymentperiod/{paymentPeriodId}")
	public ResponseEntity<?> getPaymentPeriod(@PathVariable String paymentPeriodId, @RequestParam String authToken) {
		PaymentPeriod dbPaymentPeriod = mastersService.getPaymentPeriod(paymentPeriodId, authToken);
		return new ResponseEntity<>(dbPaymentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentPeriod.class, value = "Create PaymentPeriod") // label for swagger
	@PostMapping("/paymentperiod")
	public ResponseEntity<?> postPaymentPeriod(@Valid @RequestBody AddPaymentPeriod newPaymentPeriod,
											   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		PaymentPeriod createdPaymentPeriod = mastersService.createPaymentPeriod(newPaymentPeriod, loginUserID, authToken);
		return new ResponseEntity<>(createdPaymentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentPeriod.class, value = "Update PaymentPeriod") // label for swagger
	@PatchMapping("/paymentperiod/{paymentPeriodId}")
	public ResponseEntity<?> patchPaymentPeriod(@PathVariable String paymentPeriodId,
												@RequestParam String loginUserID,
												@Valid @RequestBody UpdatePaymentPeriod updatePaymentPeriod,
												@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PaymentPeriod updatedPaymentPeriod = mastersService.updatePaymentPeriod(paymentPeriodId, loginUserID,
				updatePaymentPeriod, authToken);
		return new ResponseEntity<>(updatedPaymentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentPeriod.class, value = "Delete PaymentPeriod") // label for swagger
	@DeleteMapping("/paymentperiod/{paymentPeriodId}")
	public ResponseEntity<?> deletePaymentPeriod(@PathVariable String paymentPeriodId, @RequestParam String loginUserID,
												 @RequestParam String authToken) {
		mastersService.deletePaymentPeriod(paymentPeriodId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------PaymentTerm------------------------------------------------------------
	@ApiOperation(response = PaymentTerm.class, value = "Get all PaymentTerm details") // label for swagger
	@GetMapping("/paymentterm")
	public ResponseEntity<?> getAllPaymentTerm(@RequestParam String authToken) {
		PaymentTerm[] paymentTermList = mastersService.getAllPaymentTerm(authToken);
		return new ResponseEntity<>(paymentTermList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentTerm.class, value = "Get a PaymentTerm") // label for swagger
	@GetMapping("/paymentterm/{paymentTermId}")
	public ResponseEntity<?> getPaymentTerm(@PathVariable String paymentTermId, @RequestParam String authToken) {
		PaymentTerm dbPaymentTerm = mastersService.getPaymentTerm(paymentTermId, authToken);
		return new ResponseEntity<>(dbPaymentTerm, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentTerm.class, value = "Create PaymentTerm") // label for swagger
	@PostMapping("/paymentterm")
	public ResponseEntity<?> postPaymentTerm(@Valid @RequestBody AddPaymentTerm newPaymentTerm,
											 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		PaymentTerm createdPaymentTerm = mastersService.createPaymentTerm(newPaymentTerm, loginUserID, authToken);
		return new ResponseEntity<>(createdPaymentTerm, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentTerm.class, value = "Update PaymentTerm") // label for swagger
	@PatchMapping("/paymentterm/{paymentTermId}")
	public ResponseEntity<?> patchPaymentTerm(@PathVariable String paymentTermId,
											  @RequestParam String loginUserID,
											  @Valid @RequestBody UpdatePaymentTerm updatePaymentTerm,
											  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PaymentTerm updatedPaymentTerm = mastersService.updatePaymentTerm(paymentTermId, loginUserID,
				updatePaymentTerm, authToken);
		return new ResponseEntity<>(updatedPaymentTerm, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentTerm.class, value = "Delete PaymentTerm") // label for swagger
	@DeleteMapping("/paymentterm/{paymentTermId}")
	public ResponseEntity<?> deletePaymentTerm(@PathVariable String paymentTermId, @RequestParam String loginUserID,
											   @RequestParam String authToken) {
		mastersService.deletePaymentTerm(paymentTermId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------PaymentType------------------------------------------------------------
	@ApiOperation(response = PaymentType.class, value = "Get all PaymentType details") // label for swagger
	@GetMapping("/paymenttype")
	public ResponseEntity<?> getAllPaymentType(@RequestParam String authToken) {
		PaymentType[] paymentTypeList = mastersService.getAllPaymentType(authToken);
		return new ResponseEntity<>(paymentTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentType.class, value = "Get a PaymentType") // label for swagger
	@GetMapping("/paymenttype/{paymentTypeId}")
	public ResponseEntity<?> getPaymentType(@PathVariable String paymentTypeId, @RequestParam String authToken) {
		PaymentType dbPaymentType = mastersService.getPaymentType(paymentTypeId, authToken);
		return new ResponseEntity<>(dbPaymentType, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentType.class, value = "Create PaymentType") // label for swagger
	@PostMapping("/paymenttype")
	public ResponseEntity<?> postPaymentType(@Valid @RequestBody AddPaymentType newPaymentType,
											 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		PaymentType createdPaymentType = mastersService.createPaymentType(newPaymentType, loginUserID, authToken);
		return new ResponseEntity<>(createdPaymentType, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentType.class, value = "Update PaymentType") // label for swagger
	@PatchMapping("/paymenttype/{paymentTypeId}")
	public ResponseEntity<?> patchPaymentType(@PathVariable String paymentTypeId,
											  @RequestParam String loginUserID,
											  @Valid @RequestBody UpdatePaymentType updatePaymentType,
											  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PaymentType updatedPaymentType = mastersService.updatePaymentType(paymentTypeId, loginUserID,
				updatePaymentType, authToken);
		return new ResponseEntity<>(updatedPaymentType, HttpStatus.OK);
	}

	@ApiOperation(response = PaymentType.class, value = "Delete PaymentType") // label for swagger
	@DeleteMapping("/paymenttype/{paymentTypeId}")
	public ResponseEntity<?> deletePaymentType(@PathVariable String paymentTypeId, @RequestParam String loginUserID,
											   @RequestParam String authToken) {
		mastersService.deletePaymentType(paymentTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Phase------------------------------------------------------------
	@ApiOperation(response = Phase.class, value = "Get all Phase details") // label for swagger
	@GetMapping("/phase")
	public ResponseEntity<?> getAllPhase(@RequestParam String authToken) {
		Phase[] phaseList = mastersService.getAllPhase(authToken);
		return new ResponseEntity<>(phaseList, HttpStatus.OK);
	}

	@ApiOperation(response = Phase.class, value = "Get a Phase") // label for swagger
	@GetMapping("/phase/{phaseId}")
	public ResponseEntity<?> getPhase(@PathVariable String phaseId,@RequestParam String authToken) {
		Phase dbPhase = mastersService.getPhase(phaseId, authToken);
		return new ResponseEntity<>(dbPhase, HttpStatus.OK);
	}

	@ApiOperation(response = Phase.class, value = "Create Phase") // label for swagger
	@PostMapping("/phase")
	public ResponseEntity<?> postPhase(@Valid @RequestBody AddPhase newPhase,
									   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Phase createdPhase = mastersService.createPhase(newPhase, loginUserID, authToken);
		return new ResponseEntity<>(createdPhase, HttpStatus.OK);
	}

	@ApiOperation(response = Phase.class, value = "Update Phase") // label for swagger
	@PatchMapping("/phase/{phaseId}")
	public ResponseEntity<?> patchPhase(@PathVariable String phaseId,
										@RequestParam String loginUserID,
										@Valid @RequestBody UpdatePhase updatePhase,
										@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Phase updatedPhase = mastersService.updatePhase(phaseId, loginUserID,
				updatePhase, authToken);
		return new ResponseEntity<>(updatedPhase, HttpStatus.OK);
	}

	@ApiOperation(response = Phase.class, value = "Delete Phase") // label for swagger
	@DeleteMapping("/phase/{phaseId}")
	public ResponseEntity<?> deletePhase(@PathVariable String phaseId, @RequestParam String loginUserID,
										 @RequestParam String authToken) {
		mastersService.deletePhase(phaseId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Rack------------------------------------------------------------
	@ApiOperation(response = Rack.class, value = "Get all Rack details") // label for swagger
	@GetMapping("/rack")
	public ResponseEntity<?> getAllRack(@RequestParam String authToken) {
		Rack[] rackList = mastersService.getAllRack(authToken);
		return new ResponseEntity<>(rackList, HttpStatus.OK);
	}

	@ApiOperation(response = Rack.class, value = "Get a Rack") // label for swagger
	@GetMapping("/rack/{rackId}")
	public ResponseEntity<?> getRack(@PathVariable String rackId,@RequestParam String authToken) {
		Rack dbRack = mastersService.getRack(rackId, authToken);
		return new ResponseEntity<>(dbRack, HttpStatus.OK);
	}

	@ApiOperation(response = Rack.class, value = "Create Rack") // label for swagger
	@PostMapping("/rack")
	public ResponseEntity<?> postRack(@Valid @RequestBody AddRack newRack,
									  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Rack createdRack = mastersService.createRack(newRack, loginUserID, authToken);
		return new ResponseEntity<>(createdRack, HttpStatus.OK);
	}

	@ApiOperation(response = Rack.class, value = "Update Rack") // label for swagger
	@PatchMapping("/rack/{rackId}")
	public ResponseEntity<?> patchRack(@PathVariable String rackId,
									   @RequestParam String loginUserID,
									   @Valid @RequestBody UpdateRack updateRack,
									   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Rack updatedRack = mastersService.updateRack(rackId, loginUserID,
				updateRack, authToken);
		return new ResponseEntity<>(updatedRack, HttpStatus.OK);
	}

	@ApiOperation(response = Rack.class, value = "Delete Rack") // label for swagger
	@DeleteMapping("/rack/{rackId}")
	public ResponseEntity<?> deleteRack(@PathVariable String rackId, @RequestParam String loginUserID,
										@RequestParam String authToken) {
		mastersService.deleteRack(rackId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------RentPeriod------------------------------------------------------------
	@ApiOperation(response = RentPeriod.class, value = "Get all RentPeriod details") // label for swagger
	@GetMapping("/rentperiod")
	public ResponseEntity<?> getAllRentPeriod(@RequestParam String authToken) {
		RentPeriod[] rentPeriodList = mastersService.getAllRentPeriod(authToken);
		return new ResponseEntity<>(rentPeriodList, HttpStatus.OK);
	}

	@ApiOperation(response = RentPeriod.class, value = "Get a RentPeriod") // label for swagger
	@GetMapping("/rentperiod/{rentPeriodId}")
	public ResponseEntity<?> getRentPeriod(@PathVariable String rentPeriodId, @RequestParam String authToken) {
		RentPeriod dbRentPeriod = mastersService.getRentPeriod(rentPeriodId, authToken);
		return new ResponseEntity<>(dbRentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = RentPeriod.class, value = "Create RentPeriod") // label for swagger
	@PostMapping("/rentperiod")
	public ResponseEntity<?> postRentPeriod(@Valid @RequestBody AddRentPeriod newRentPeriod,
											@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		RentPeriod createdRentPeriod = mastersService.createRentPeriod(newRentPeriod, loginUserID, authToken);
		return new ResponseEntity<>(createdRentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = RentPeriod.class, value = "Update RentPeriod") // label for swagger
	@PatchMapping("/rentperiod/{rentPeriodId}")
	public ResponseEntity<?> patchRentPeriod(@PathVariable String rentPeriodId,
											 @RequestParam String loginUserID,
											 @Valid @RequestBody UpdateRentPeriod updateRentPeriod,
											 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		RentPeriod updatedRentPeriod = mastersService.updateRentPeriod(rentPeriodId, loginUserID,
				updateRentPeriod, authToken);
		return new ResponseEntity<>(updatedRentPeriod, HttpStatus.OK);
	}

	@ApiOperation(response = RentPeriod.class, value = "Delete RentPeriod") // label for swagger
	@DeleteMapping("/rentperiod/{rentPeriodId}")
	public ResponseEntity<?> deleteRentPeriod(@PathVariable String rentPeriodId, @RequestParam String loginUserID,
											  @RequestParam String authToken) {
		mastersService.deleteRentPeriod(rentPeriodId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------RequirementType------------------------------------------------------------
	@ApiOperation(response = RequirementType.class, value = "Get all RequirementType details") // label for swagger
	@GetMapping("/requirementtype")
	public ResponseEntity<?> getAllRequirementType(@RequestParam String authToken) {
		RequirementType[] requirementTypeList = mastersService.getAllRequirementType(authToken);
		return new ResponseEntity<>(requirementTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = RequirementType.class, value = "Get a RequirementType") // label for swagger
	@GetMapping("/requirementtype/{requirementTypeId}")
	public ResponseEntity<?> getRequirementType(@PathVariable String requirementTypeId, @RequestParam String authToken) {
		RequirementType dbRequirementType = mastersService.getRequirementType(requirementTypeId, authToken);
		return new ResponseEntity<>(dbRequirementType, HttpStatus.OK);
	}

	@ApiOperation(response = RequirementType.class, value = "Create RequirementType") // label for swagger
	@PostMapping("/requirementtype")
	public ResponseEntity<?> postRequirementType(@Valid @RequestBody AddRequirementType newRequirementType,
												 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		RequirementType createdRequirementType = mastersService.createRequirementType(newRequirementType, loginUserID, authToken);
		return new ResponseEntity<>(createdRequirementType, HttpStatus.OK);
	}

	@ApiOperation(response = RequirementType.class, value = "Update RequirementType") // label for swagger
	@PatchMapping("/requirementtype/{requirementTypeId}")
	public ResponseEntity<?> patchRequirementType(@PathVariable String requirementTypeId,
												  @RequestParam String loginUserID,
												  @Valid @RequestBody UpdateRequirementType updateRequirementType,
												  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		RequirementType updatedRequirementType = mastersService.updateRequirementType(requirementTypeId, loginUserID,
				updateRequirementType, authToken);
		return new ResponseEntity<>(updatedRequirementType, HttpStatus.OK);
	}

	@ApiOperation(response = RequirementType.class, value = "Delete RequirementType") // label for swagger
	@DeleteMapping("/requirementtype/{requirementTypeId}")
	public ResponseEntity<?> deleteRequirementType(@PathVariable String requirementTypeId, @RequestParam String loginUserID,
												   @RequestParam String authToken) {
		mastersService.deleteRequirementType(requirementTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Room------------------------------------------------------------
	@ApiOperation(response = Room.class, value = "Get all Room details") // label for swagger
	@GetMapping("/room")
	public ResponseEntity<?> getAllRoom(@RequestParam String authToken) {
		Room[] roomList = mastersService.getAllRoom(authToken);
		return new ResponseEntity<>(roomList, HttpStatus.OK);
	}

	@ApiOperation(response = Room.class, value = "Get a Room") // label for swagger
	@GetMapping("/room/{roomId}")
	public ResponseEntity<?> getRoom(@PathVariable String roomId,@RequestParam String authToken) {
		Room dbRoom = mastersService.getRoom(roomId, authToken);
		return new ResponseEntity<>(dbRoom, HttpStatus.OK);
	}

	@ApiOperation(response = Room.class, value = "Create Room") // label for swagger
	@PostMapping("/room")
	public ResponseEntity<?> postRoom(@Valid @RequestBody AddRoom newRoom,
									  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Room createdRoom = mastersService.createRoom(newRoom, loginUserID, authToken);
		return new ResponseEntity<>(createdRoom, HttpStatus.OK);
	}

	@ApiOperation(response = Room.class, value = "Update Room") // label for swagger
	@PatchMapping("/room/{roomId}")
	public ResponseEntity<?> patchRoom(@PathVariable String roomId,
									   @RequestParam String loginUserID,
									   @Valid @RequestBody UpdateRoom updateRoom,
									   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Room updatedRoom = mastersService.updateRoom(roomId, loginUserID,
				updateRoom, authToken);
		return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
	}

	@ApiOperation(response = Room.class, value = "Delete Room") // label for swagger
	@DeleteMapping("/room/{roomId}")
	public ResponseEntity<?> deleteRoom(@PathVariable String roomId, @RequestParam String loginUserID,
										@RequestParam String authToken) {
		mastersService.deleteRoom(roomId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------ServiceRendered------------------------------------------------------------
	@ApiOperation(response = ServiceRendered.class, value = "Get all ServiceRendered details") // label for swagger
	@GetMapping("/servicerendered")
	public ResponseEntity<?> getAllServiceRendered(@RequestParam String authToken) {
		ServiceRendered[] serviceRenderedList = mastersService.getAllServiceRendered(authToken);
		return new ResponseEntity<>(serviceRenderedList, HttpStatus.OK);
	}

	@ApiOperation(response = ServiceRendered.class, value = "Get a ServiceRendered") // label for swagger
	@GetMapping("/servicerendered/{serviceRenderedId}")
	public ResponseEntity<?> getServiceRendered(@PathVariable String serviceRenderedId, @RequestParam String authToken) {
		ServiceRendered dbServiceRendered = mastersService.getServiceRendered(serviceRenderedId, authToken);
		return new ResponseEntity<>(dbServiceRendered, HttpStatus.OK);
	}

	@ApiOperation(response = ServiceRendered.class, value = "Create ServiceRendered") // label for swagger
	@PostMapping("/servicerendered")
	public ResponseEntity<?> postServiceRendered(@Valid @RequestBody AddServiceRendered newServiceRendered,
												 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		ServiceRendered createdServiceRendered = mastersService.createServiceRendered(newServiceRendered, loginUserID, authToken);
		return new ResponseEntity<>(createdServiceRendered, HttpStatus.OK);
	}

	@ApiOperation(response = ServiceRendered.class, value = "Update ServiceRendered") // label for swagger
	@PatchMapping("/servicerendered/{serviceRenderedId}")
	public ResponseEntity<?> patchServiceRendered(@PathVariable String serviceRenderedId,
												  @RequestParam String loginUserID,
												  @Valid @RequestBody UpdateServiceRendered updateServiceRendered,
												  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ServiceRendered updatedServiceRendered = mastersService.updateServiceRendered(serviceRenderedId, loginUserID,
				updateServiceRendered, authToken);
		return new ResponseEntity<>(updatedServiceRendered, HttpStatus.OK);
	}

	@ApiOperation(response = ServiceRendered.class, value = "Delete ServiceRendered") // label for swagger
	@DeleteMapping("/servicerendered/{serviceRenderedId}")
	public ResponseEntity<?> deleteServiceRendered(@PathVariable String serviceRenderedId, @RequestParam String loginUserID,
												   @RequestParam String authToken) {
		mastersService.deleteServiceRendered(serviceRenderedId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Status------------------------------------------------------------
	@ApiOperation(response = Status.class, value = "Get all Status details") // label for swagger
	@GetMapping("/status")
	public ResponseEntity<?> getAllStatus(@RequestParam String authToken) {
		Status[] statusList = mastersService.getAllStatus(authToken);
		return new ResponseEntity<>(statusList, HttpStatus.OK);
	}

	@ApiOperation(response = Status.class, value = "Get a Status") // label for swagger
	@GetMapping("/status/{statusId}")
	public ResponseEntity<?> getStatus(@PathVariable String statusId,@RequestParam String authToken) {
		Status dbStatus = mastersService.getStatus(statusId, authToken);
		return new ResponseEntity<>(dbStatus, HttpStatus.OK);
	}

	@ApiOperation(response = Status.class, value = "Create Status") // label for swagger
	@PostMapping("/status")
	public ResponseEntity<?> postStatus(@Valid @RequestBody AddStatus newStatus,
										@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Status createdStatus = mastersService.createStatus(newStatus, loginUserID, authToken);
		return new ResponseEntity<>(createdStatus, HttpStatus.OK);
	}

	@ApiOperation(response = Status.class, value = "Update Status") // label for swagger
	@PatchMapping("/status/{statusId}")
	public ResponseEntity<?> patchStatus(@PathVariable String statusId,
										 @RequestParam String loginUserID,
										 @Valid @RequestBody UpdateStatus updateStatus,
										 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Status updatedStatus = mastersService.updateStatus(statusId, loginUserID,
				updateStatus, authToken);
		return new ResponseEntity<>(updatedStatus, HttpStatus.OK);
	}

	@ApiOperation(response = Status.class, value = "Delete Status") // label for swagger
	@DeleteMapping("/status/{statusId}")
	public ResponseEntity<?> deleteStatus(@PathVariable String statusId, @RequestParam String loginUserID,
										  @RequestParam String authToken) {
		mastersService.deleteStatus(statusId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------StorageType------------------------------------------------------------
	@ApiOperation(response = StorageType.class, value = "Get all StorageType details") // label for swagger
	@GetMapping("/storagetype")
	public ResponseEntity<?> getAllStorageType(@RequestParam String authToken) {
		StorageType[] storageTypeList = mastersService.getAllStorageType(authToken);
		return new ResponseEntity<>(storageTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = StorageType.class, value = "Get a StorageType") // label for swagger
	@GetMapping("/storagetype/{storageTypeId}")
	public ResponseEntity<?> getStorageType(@PathVariable String storageTypeId, @RequestParam String authToken) {
		StorageType dbStorageType = mastersService.getStorageType(storageTypeId, authToken);
		return new ResponseEntity<>(dbStorageType, HttpStatus.OK);
	}

	@ApiOperation(response = StorageType.class, value = "Create StorageType") // label for swagger
	@PostMapping("/storagetype")
	public ResponseEntity<?> postStorageType(@Valid @RequestBody AddStorageType newStorageType,
											 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		StorageType createdStorageType = mastersService.createStorageType(newStorageType, loginUserID, authToken);
		return new ResponseEntity<>(createdStorageType, HttpStatus.OK);
	}

	@ApiOperation(response = StorageType.class, value = "Update StorageType") // label for swagger
	@PatchMapping("/storagetype/{storageTypeId}")
	public ResponseEntity<?> patchStorageType(@PathVariable String storageTypeId,
											  @RequestParam String loginUserID,
											  @Valid @RequestBody UpdateStorageType updateStorageType,
											  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		StorageType updatedStorageType = mastersService.updateStorageType(storageTypeId, loginUserID,
				updateStorageType, authToken);
		return new ResponseEntity<>(updatedStorageType, HttpStatus.OK);
	}

	@ApiOperation(response = StorageType.class, value = "Delete StorageType") // label for swagger
	@DeleteMapping("/storagetype/{storageTypeId}")
	public ResponseEntity<?> deleteStorageType(@PathVariable String storageTypeId, @RequestParam String loginUserID,
											   @RequestParam String authToken) {
		mastersService.deleteStorageType(storageTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------StoreNumberSize------------------------------------------------------------
	@ApiOperation(response = StoreNumberSize.class, value = "Get all StoreNumberSize details") // label for swagger
	@GetMapping("/storenumbersize")
	public ResponseEntity<?> getAllStoreNumberSize(@RequestParam String authToken) {
		StoreNumberSize[] storeNumberSizeList = mastersService.getAllStoreNumberSize(authToken);
		return new ResponseEntity<>(storeNumberSizeList, HttpStatus.OK);
	}

	@ApiOperation(response = StoreNumberSize.class, value = "Get a StoreNumberSize") // label for swagger
	@GetMapping("/storenumbersize/{storeNumberSizeId}")
	public ResponseEntity<?> getStoreNumberSize(@PathVariable String storeNumberSizeId, @RequestParam String authToken) {
		StoreNumberSize dbStoreNumberSize = mastersService.getStoreNumberSize(storeNumberSizeId, authToken);
		return new ResponseEntity<>(dbStoreNumberSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreNumberSize.class, value = "Create StoreNumberSize") // label for swagger
	@PostMapping("/storenumbersize")
	public ResponseEntity<?> postStoreNumberSize(@Valid @RequestBody AddStoreNumberSize newStoreNumberSize,
												 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		StoreNumberSize createdStoreNumberSize = mastersService.createStoreNumberSize(newStoreNumberSize, loginUserID, authToken);
		return new ResponseEntity<>(createdStoreNumberSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreNumberSize.class, value = "Update StoreNumberSize") // label for swagger
	@PatchMapping("/storenumbersize/{storeNumberSizeId}")
	public ResponseEntity<?> patchStoreNumberSize(@PathVariable String storeNumberSizeId,
												  @RequestParam String loginUserID,
												  @Valid @RequestBody UpdateStoreNumberSize updateStoreNumberSize,
												  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		StoreNumberSize updatedStoreNumberSize = mastersService.updateStoreNumberSize(storeNumberSizeId, loginUserID,
				updateStoreNumberSize, authToken);
		return new ResponseEntity<>(updatedStoreNumberSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreNumberSize.class, value = "Delete StoreNumberSize") // label for swagger
	@DeleteMapping("/storenumbersize/{storeNumberSizeId}")
	public ResponseEntity<?> deleteStoreNumberSize(@PathVariable String storeNumberSizeId, @RequestParam String loginUserID,
												   @RequestParam String authToken) {
		mastersService.deleteStoreNumberSize(storeNumberSizeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------StoreSize------------------------------------------------------------
	@ApiOperation(response = StoreSize.class, value = "Get all StoreSize details") // label for swagger
	@GetMapping("/storesize")
	public ResponseEntity<?> getAllStoreSize(@RequestParam String authToken) {
		StoreSize[] storeSizeList = mastersService.getAllStoreSize(authToken);
		return new ResponseEntity<>(storeSizeList, HttpStatus.OK);
	}

	@ApiOperation(response = StoreSize.class, value = "Get a StoreSize") // label for swagger
	@GetMapping("/storesize/{storeSizeId}")
	public ResponseEntity<?> getStoreSize(@PathVariable String storeSizeId, @RequestParam String authToken) {
		StoreSize dbStoreSize = mastersService.getStoreSize(storeSizeId, authToken);
		return new ResponseEntity<>(dbStoreSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreSize.class, value = "Create StoreSize") // label for swagger
	@PostMapping("/storesize")
	public ResponseEntity<?> postStoreSize(@Valid @RequestBody AddStoreSize newStoreSize,
										   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		StoreSize createdStoreSize = mastersService.createStoreSize(newStoreSize, loginUserID, authToken);
		return new ResponseEntity<>(createdStoreSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreSize.class, value = "Update StoreSize") // label for swagger
	@PatchMapping("/storesize/{storeSizeId}")
	public ResponseEntity<?> patchStoreSize(@PathVariable String storeSizeId,
											@RequestParam String loginUserID,
											@Valid @RequestBody UpdateStoreSize updateStoreSize,
											@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		StoreSize updatedStoreSize = mastersService.updateStoreSize(storeSizeId, loginUserID,
				updateStoreSize, authToken);
		return new ResponseEntity<>(updatedStoreSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreSize.class, value = "Delete StoreSize") // label for swagger
	@DeleteMapping("/storesize/{storeSizeId}")
	public ResponseEntity<?> deleteStoreSize(@PathVariable String storeSizeId, @RequestParam String loginUserID,
											 @RequestParam String authToken) {
		mastersService.deleteStoreSize(storeSizeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------UnitOfMeasure------------------------------------------------------------
	@ApiOperation(response = UnitOfMeasure.class, value = "Get all UnitOfMeasure details") // label for swagger
	@GetMapping("/unitofmeasure")
	public ResponseEntity<?> getAllUnitOfMeasure(@RequestParam String authToken) {
		UnitOfMeasure[] unitOfMeasureList = mastersService.getAllUnitOfMeasure(authToken);
		return new ResponseEntity<>(unitOfMeasureList, HttpStatus.OK);
	}

	@ApiOperation(response = UnitOfMeasure.class, value = "Get a UnitOfMeasure") // label for swagger
	@GetMapping("/unitofmeasure/{unitOfMeasureId}")
	public ResponseEntity<?> getUnitOfMeasure(@PathVariable String unitOfMeasureId, @RequestParam String authToken) {
		UnitOfMeasure dbUnitOfMeasure = mastersService.getUnitOfMeasure(unitOfMeasureId, authToken);
		return new ResponseEntity<>(dbUnitOfMeasure, HttpStatus.OK);
	}

	@ApiOperation(response = UnitOfMeasure.class, value = "Create UnitOfMeasure") // label for swagger
	@PostMapping("/unitofmeasure")
	public ResponseEntity<?> postUnitOfMeasure(@Valid @RequestBody AddUnitOfMeasure newUnitOfMeasure,
											   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		UnitOfMeasure createdUnitOfMeasure = mastersService.createUnitOfMeasure(newUnitOfMeasure, loginUserID, authToken);
		return new ResponseEntity<>(createdUnitOfMeasure, HttpStatus.OK);
	}

	@ApiOperation(response = UnitOfMeasure.class, value = "Update UnitOfMeasure") // label for swagger
	@PatchMapping("/unitofmeasure/{unitOfMeasureId}")
	public ResponseEntity<?> patchUnitOfMeasure(@PathVariable String unitOfMeasureId,
												@RequestParam String loginUserID,
												@Valid @RequestBody UpdateUnitOfMeasure updateUnitOfMeasure,
												@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		UnitOfMeasure updatedUnitOfMeasure = mastersService.updateUnitOfMeasure(unitOfMeasureId, loginUserID,
				updateUnitOfMeasure, authToken);
		return new ResponseEntity<>(updatedUnitOfMeasure, HttpStatus.OK);
	}

	@ApiOperation(response = UnitOfMeasure.class, value = "Delete UnitOfMeasure") // label for swagger
	@DeleteMapping("/unitofmeasure/{unitOfMeasureId}")
	public ResponseEntity<?> deleteUnitOfMeasure(@PathVariable String unitOfMeasureId, @RequestParam String loginUserID,
												 @RequestParam String authToken) {
		mastersService.deleteUnitOfMeasure(unitOfMeasureId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Warehouse------------------------------------------------------------
	@ApiOperation(response = Warehouse.class, value = "Get all Warehouse details") // label for swagger
	@GetMapping("/warehouse")
	public ResponseEntity<?> getAllWarehouse(@RequestParam String authToken) {
		Warehouse[] warehouseList = mastersService.getAllWarehouse(authToken);
		return new ResponseEntity<>(warehouseList, HttpStatus.OK);
	}

	@ApiOperation(response = Warehouse.class, value = "Get a Warehouse") // label for swagger
	@GetMapping("/warehouse/{warehouseId}")
	public ResponseEntity<?> getWarehouse(@PathVariable String warehouseId, @RequestParam String authToken) {
		Warehouse dbWarehouse = mastersService.getWarehouse(warehouseId, authToken);
		return new ResponseEntity<>(dbWarehouse, HttpStatus.OK);
	}

	@ApiOperation(response = Warehouse.class, value = "Create Warehouse") // label for swagger
	@PostMapping("/warehouse")
	public ResponseEntity<?> postWarehouse(@Valid @RequestBody AddWarehouse newWarehouse,
										   @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Warehouse createdWarehouse = mastersService.createWarehouse(newWarehouse, loginUserID, authToken);
		return new ResponseEntity<>(createdWarehouse, HttpStatus.OK);
	}

	@ApiOperation(response = Warehouse.class, value = "Update Warehouse") // label for swagger
	@PatchMapping("/warehouse/{warehouseId}")
	public ResponseEntity<?> patchWarehouse(@PathVariable String warehouseId,
											@RequestParam String loginUserID,
											@Valid @RequestBody UpdateWarehouse updateWarehouse,
											@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Warehouse updatedWarehouse = mastersService.updateWarehouse(warehouseId, loginUserID,
				updateWarehouse, authToken);
		return new ResponseEntity<>(updatedWarehouse, HttpStatus.OK);
	}

	@ApiOperation(response = Warehouse.class, value = "Delete Warehouse") // label for swagger
	@DeleteMapping("/warehouse/{warehouseId}")
	public ResponseEntity<?> deleteWarehouse(@PathVariable String warehouseId, @RequestParam String loginUserID,
											 @RequestParam String authToken) {
		mastersService.deleteWarehouse(warehouseId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------WorkOrderCreatedBy------------------------------------------------------------
	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Get all WorkOrderCreatedBy details") // label for swagger
	@GetMapping("/workordercreatedby")
	public ResponseEntity<?> getAllWorkOrderCreatedBy(@RequestParam String authToken) {
		WorkOrderCreatedBy[] workOrderCreatedByList = mastersService.getAllWorkOrderCreatedBy(authToken);
		return new ResponseEntity<>(workOrderCreatedByList, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Get a WorkOrderCreatedBy") // label for swagger
	@GetMapping("/workordercreatedby/{workOrderCreatedById}")
	public ResponseEntity<?> getWorkOrderCreatedBy(@PathVariable String workOrderCreatedById, @RequestParam String authToken) {
		WorkOrderCreatedBy dbWorkOrderCreatedBy = mastersService.getWorkOrderCreatedBy(workOrderCreatedById, authToken);
		return new ResponseEntity<>(dbWorkOrderCreatedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Create WorkOrderCreatedBy") // label for swagger
	@PostMapping("/workordercreatedby")
	public ResponseEntity<?> postWorkOrderCreatedBy(@Valid @RequestBody AddWorkOrderCreatedBy newWorkOrderCreatedBy,
													@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		WorkOrderCreatedBy createdWorkOrderCreatedBy = mastersService.createWorkOrderCreatedBy(newWorkOrderCreatedBy, loginUserID, authToken);
		return new ResponseEntity<>(createdWorkOrderCreatedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Update WorkOrderCreatedBy") // label for swagger
	@PatchMapping("/workordercreatedby/{workOrderCreatedById}")
	public ResponseEntity<?> patchWorkOrderCreatedBy(@PathVariable String workOrderCreatedById,
													 @RequestParam String loginUserID,
													 @Valid @RequestBody UpdateWorkOrderCreatedBy updateWorkOrderCreatedBy,
													 @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrderCreatedBy updatedWorkOrderCreatedBy = mastersService.updateWorkOrderCreatedBy(workOrderCreatedById, loginUserID,
				updateWorkOrderCreatedBy, authToken);
		return new ResponseEntity<>(updatedWorkOrderCreatedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderCreatedBy.class, value = "Delete WorkOrderCreatedBy") // label for swagger
	@DeleteMapping("/workordercreatedby/{workOrderCreatedById}")
	public ResponseEntity<?> deleteWorkOrderCreatedBy(@PathVariable String workOrderCreatedById, @RequestParam String loginUserID,
													  @RequestParam String authToken) {
		mastersService.deleteWorkOrderCreatedBy(workOrderCreatedById, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------WorkOrderProcessedBy------------------------------------------------------------
	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Get all WorkOrderProcessedBy details") // label for swagger
	@GetMapping("/workorderprocessedby")
	public ResponseEntity<?> getAllWorkOrderProcessedBy(@RequestParam String authToken) {
		WorkOrderProcessedBy[] workOrderProcessedByList = mastersService.getAllWorkOrderProcessedBy(authToken);
		return new ResponseEntity<>(workOrderProcessedByList, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Get a WorkOrderProcessedBy") // label for swagger
	@GetMapping("/workorderprocessedby/{workOrderProcessedById}")
	public ResponseEntity<?> getWorkOrderProcessedBy(@PathVariable String workOrderProcessedById, @RequestParam String authToken) {
		WorkOrderProcessedBy dbWorkOrderProcessedBy = mastersService.getWorkOrderProcessedBy(workOrderProcessedById, authToken);
		return new ResponseEntity<>(dbWorkOrderProcessedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Create WorkOrderProcessedBy") // label for swagger
	@PostMapping("/workorderprocessedby")
	public ResponseEntity<?> postWorkOrderProcessedBy(@Valid @RequestBody AddWorkOrderProcessedBy newWorkOrderProcessedBy,
													  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		WorkOrderProcessedBy createdWorkOrderProcessedBy = mastersService.createWorkOrderProcessedBy(newWorkOrderProcessedBy, loginUserID, authToken);
		return new ResponseEntity<>(createdWorkOrderProcessedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Update WorkOrderProcessedBy") // label for swagger
	@PatchMapping("/workorderprocessedby/{workOrderProcessedById}")
	public ResponseEntity<?> patchWorkOrderProcessedBy(@PathVariable String workOrderProcessedById,
													   @RequestParam String loginUserID,
													   @Valid @RequestBody UpdateWorkOrderProcessedBy updateWorkOrderProcessedBy,
													   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrderProcessedBy updatedWorkOrderProcessedBy = mastersService.updateWorkOrderProcessedBy(workOrderProcessedById, loginUserID,
				updateWorkOrderProcessedBy, authToken);
		return new ResponseEntity<>(updatedWorkOrderProcessedBy, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderProcessedBy.class, value = "Delete WorkOrderProcessedBy") // label for swagger
	@DeleteMapping("/workorderprocessedby/{workOrderProcessedById}")
	public ResponseEntity<?> deleteWorkOrderProcessedBy(@PathVariable String workOrderProcessedById, @RequestParam String loginUserID,
														@RequestParam String authToken) {
		mastersService.deleteWorkOrderProcessedBy(workOrderProcessedById, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------WorkOrderStatus------------------------------------------------------------
	@ApiOperation(response = WorkOrderStatus.class, value = "Get all WorkOrderStatus details") // label for swagger
	@GetMapping("/workorderstatus")
	public ResponseEntity<?> getAllWorkOrderStatus(@RequestParam String authToken) {
		WorkOrderStatus[] workOrderStatusList = mastersService.getAllWorkOrderStatus(authToken);
		return new ResponseEntity<>(workOrderStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderStatus.class, value = "Get a WorkOrderStatus") // label for swagger
	@GetMapping("/workorderstatus/{workOrderStatusId}")
	public ResponseEntity<?> getWorkOrderStatus(@PathVariable String workOrderStatusId, @RequestParam String authToken) {
		WorkOrderStatus dbWorkOrderStatus = mastersService.getWorkOrderStatus(workOrderStatusId, authToken);
		return new ResponseEntity<>(dbWorkOrderStatus, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderStatus.class, value = "Create WorkOrderStatus") // label for swagger
	@PostMapping("/workorderstatus")
	public ResponseEntity<?> postWorkOrderStatus(@Valid @RequestBody AddWorkOrderStatus newWorkOrderStatus,
												 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		WorkOrderStatus createdWorkOrderStatus = mastersService.createWorkOrderStatus(newWorkOrderStatus, loginUserID, authToken);
		return new ResponseEntity<>(createdWorkOrderStatus, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderStatus.class, value = "Update WorkOrderStatus") // label for swagger
	@PatchMapping("/workorderstatus/{workOrderStatusId}")
	public ResponseEntity<?> patchWorkOrderStatus(@PathVariable String workOrderStatusId,
												  @RequestParam String loginUserID,
												  @Valid @RequestBody UpdateWorkOrderStatus updateWorkOrderStatus,
												  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		WorkOrderStatus updatedWorkOrderStatus = mastersService.updateWorkOrderStatus(workOrderStatusId, loginUserID,
				updateWorkOrderStatus, authToken);
		return new ResponseEntity<>(updatedWorkOrderStatus, HttpStatus.OK);
	}

	@ApiOperation(response = WorkOrderStatus.class, value = "Delete WorkOrderStatus") // label for swagger
	@DeleteMapping("/workorderstatus/{workOrderStatusId}")
	public ResponseEntity<?> deleteWorkOrderStatus(@PathVariable String workOrderStatusId, @RequestParam String loginUserID,
												   @RequestParam String authToken) {
		mastersService.deleteWorkOrderStatus(workOrderStatusId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Zone------------------------------------------------------------
	@ApiOperation(response = Zone.class, value = "Get all Zone details") // label for swagger
	@GetMapping("/zone")
	public ResponseEntity<?> getAllZone(@RequestParam String authToken) {
		Zone[] zoneList = mastersService.getAllZone(authToken);
		return new ResponseEntity<>(zoneList, HttpStatus.OK);
	}

	@ApiOperation(response = Zone.class, value = "Get a Zone") // label for swagger
	@GetMapping("/zone/{zoneId}")
	public ResponseEntity<?> getZone(@PathVariable String zoneId, @RequestParam String authToken) {
		Zone dbZone = mastersService.getZone(zoneId, authToken);
		return new ResponseEntity<>(dbZone, HttpStatus.OK);
	}

	@ApiOperation(response = Zone.class, value = "Create Zone") // label for swagger
	@PostMapping("/zone")
	public ResponseEntity<?> postZone(@Valid @RequestBody AddZone newZone,
									  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Zone createdZone = mastersService.createZone(newZone, loginUserID, authToken);
		return new ResponseEntity<>(createdZone, HttpStatus.OK);
	}

	@ApiOperation(response = Zone.class, value = "Update Zone") // label for swagger
	@PatchMapping("/zone/{zoneId}")
	public ResponseEntity<?> patchZone(@PathVariable String zoneId,
									   @RequestParam String loginUserID,
									   @Valid @RequestBody UpdateZone updateZone,
									   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Zone updatedZone = mastersService.updateZone(zoneId, loginUserID,
				updateZone, authToken);
		return new ResponseEntity<>(updatedZone, HttpStatus.OK);
	}

	@ApiOperation(response = Zone.class, value = "Delete Zone") // label for swagger
	@DeleteMapping("/zone/{zoneId}")
	public ResponseEntity<?> deleteZone(@PathVariable String zoneId, @RequestParam String loginUserID,
										@RequestParam String authToken) {
		mastersService.deleteZone(zoneId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Country------------------------------------------------------------
	@ApiOperation(response = Country.class, value = "Get all Country details") // label for swagger
	@GetMapping("/country")
	public ResponseEntity<?> getAllCountry(@RequestParam String authToken) {
		Country[] countryList = mastersService.getAllCountry(authToken);
		return new ResponseEntity<>(countryList, HttpStatus.OK);
	}

	@ApiOperation(response = Country.class, value = "Get a Country") // label for swagger
	@GetMapping("/country/{countryId}")
	public ResponseEntity<?> getCountry(@PathVariable String countryId, @RequestParam String authToken) {
		Country dbCountry = mastersService.getCountry(countryId, authToken);
		return new ResponseEntity<>(dbCountry, HttpStatus.OK);
	}

	@ApiOperation(response = Country.class, value = "Create Country") // label for swagger
	@PostMapping("/country")
	public ResponseEntity<?> postCountry(@Valid @RequestBody AddCountry newCountry,
										 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Country createdCountry = mastersService.createCountry(newCountry, loginUserID, authToken);
		return new ResponseEntity<>(createdCountry, HttpStatus.OK);
	}

	@ApiOperation(response = Country.class, value = "Update Country") // label for swagger
	@PatchMapping("/country/{countryId}")
	public ResponseEntity<?> patchCountry(@PathVariable String countryId,
										  @RequestParam String loginUserID,
										  @Valid @RequestBody UpdateCountry updateCountry,
										  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Country updatedCountry = mastersService.updateCountry(countryId, loginUserID,
				updateCountry, authToken);
		return new ResponseEntity<>(updatedCountry, HttpStatus.OK);
	}

	@ApiOperation(response = Country.class, value = "Delete Country") // label for swagger
	@DeleteMapping("/country/{countryId}")
	public ResponseEntity<?> deleteCountry(@PathVariable String countryId, @RequestParam String loginUserID,
										   @RequestParam String authToken) {
		mastersService.deleteCountry(countryId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Employee------------------------------------------------------------
	@ApiOperation(response = Employee.class, value = "Get all Employee details") // label for swagger
	@GetMapping("/employee")
	public ResponseEntity<?> getAllEmployee(@RequestParam String authToken) {
		Employee[] employeeList = mastersService.getAllEmployee(authToken);
		return new ResponseEntity<>(employeeList, HttpStatus.OK);
	}

	@ApiOperation(response = Employee.class, value = "Get a Employee") // label for swagger
	@GetMapping("/employee/{employeeId}")
	public ResponseEntity<?> getEmployee(@PathVariable String employeeId, @RequestParam String authToken) {
		Employee dbEmployee = mastersService.getEmployee(employeeId, authToken);
		return new ResponseEntity<>(dbEmployee, HttpStatus.OK);
	}

	@ApiOperation(response = Employee.class, value = "Create Employee") // label for swagger
	@PostMapping("/employee")
	public ResponseEntity<?> postEmployee(@Valid @RequestBody AddEmployee newEmployee,
										  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Employee createdEmployee = mastersService.createEmployee(newEmployee, loginUserID, authToken);
		return new ResponseEntity<>(createdEmployee, HttpStatus.OK);
	}

	@ApiOperation(response = Employee.class, value = "Update Employee") // label for swagger
	@PatchMapping("/employee/{employeeId}")
	public ResponseEntity<?> patchEmployee(@PathVariable String employeeId,
										   @RequestParam String loginUserID,
										   @Valid @RequestBody UpdateEmployee updateEmployee,
										   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Employee updatedEmployee = mastersService.updateEmployee(employeeId, loginUserID,
				updateEmployee, authToken);
		return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
	}

	@ApiOperation(response = Employee.class, value = "Delete Employee") // label for swagger
	@DeleteMapping("/employee/{employeeId}")
	public ResponseEntity<?> deleteEmployee(@PathVariable String employeeId, @RequestParam String loginUserID,
											@RequestParam String authToken) {
		mastersService.deleteEmployee(employeeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//-----------------------------------Sbu------------------------------------------------------------
	@ApiOperation(response = Sbu.class, value = "Get all Sbu details") // label for swagger
	@GetMapping("/sbu")
	public ResponseEntity<?> getAllSbu(@RequestParam String authToken) {
		Sbu[] sbuList = mastersService.getAllSbu(authToken);
		return new ResponseEntity<>(sbuList, HttpStatus.OK);
	}

	@ApiOperation(response = Sbu.class, value = "Get a Sbu") // label for swagger
	@GetMapping("/sbu/{sbuId}")
	public ResponseEntity<?> getSbu(@PathVariable String sbuId, @RequestParam String authToken) {
		Sbu dbSbu = mastersService.getSbu(sbuId, authToken);
		return new ResponseEntity<>(dbSbu, HttpStatus.OK);
	}

	@ApiOperation(response = Sbu.class, value = "Create Sbu") // label for swagger
	@PostMapping("/sbu")
	public ResponseEntity<?> postSbu(@Valid @RequestBody AddSbu newSbu,
									 @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		Sbu createdSbu = mastersService.createSbu(newSbu, loginUserID, authToken);
		return new ResponseEntity<>(createdSbu, HttpStatus.OK);
	}

	@ApiOperation(response = Sbu.class, value = "Update Sbu") // label for swagger
	@PatchMapping("/sbu/{sbuId}")
	public ResponseEntity<?> patchSbu(@PathVariable String sbuId,
									  @RequestParam String loginUserID,
									  @Valid @RequestBody UpdateSbu updateSbu,
									  @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		Sbu updatedSbu = mastersService.updateSbu(sbuId, loginUserID,
				updateSbu, authToken);
		return new ResponseEntity<>(updatedSbu, HttpStatus.OK);
	}

	@ApiOperation(response = Sbu.class, value = "Delete Sbu") // label for swagger
	@DeleteMapping("/sbu/{sbuId}")
	public ResponseEntity<?> deleteSbu(@PathVariable String sbuId, @RequestParam String loginUserID,
									   @RequestParam String authToken) {
		mastersService.deleteSbu(sbuId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------NumberRange--------------------------------------------------------------------
	@ApiOperation(response = NumberRange.class, value = "Get all NumberRange details") // label for swagger
	@GetMapping("/numberRange")
	public ResponseEntity<?> getNumberRanges(@RequestParam String authToken) {
		NumberRange[] numberRangeList = mastersService.getNumberRanges(authToken);
		return new ResponseEntity<>(numberRangeList, HttpStatus.OK);
	}

	@ApiOperation(response = NumberRange.class, value = "Get a NumberRange") // label for swagger
	@GetMapping("/numberRange/{numberRangeCode}")
	public ResponseEntity<?> getNumberRange(@PathVariable Long numberRangeCode, @RequestParam String authToken) {
		NumberRange numberRange = mastersService.getNumberRange(numberRangeCode, authToken);
		log.info("NumberRange : " + numberRange);
		return new ResponseEntity<>(numberRange, HttpStatus.OK);
	}

	@ApiOperation(response = NumberRange.class, value = "Get Number Range Current") // label for swagger
	@GetMapping("/numberRange/nextNumberRange")
	public ResponseEntity<?> getNumberRange(@RequestParam Long numberRangeCode, @RequestParam String description, @RequestParam String authToken) {
		String nextRangeValue = mastersService.getNextNumberRange(numberRangeCode, description, authToken);
		return new ResponseEntity<>(nextRangeValue , HttpStatus.OK);
	}

	@ApiOperation(response = NumberRange.class, value = "Create NumberRange") // label for swagger
	@PostMapping("/numberRange")
	public ResponseEntity<?> postNumberRange(@Valid @RequestBody NumberRange newNumberRange, @RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		NumberRange createdNumberRange = mastersService.createNumberRange(newNumberRange, loginUserID, authToken);
		return new ResponseEntity<>(createdNumberRange , HttpStatus.OK);
	}

	@ApiOperation(response = NumberRange.class, value = "Update NumberRange") // label for swagger
	@PatchMapping("/numberRange")
	public ResponseEntity<?> patchNumberRange(@RequestParam Long numberRangeCode, @RequestParam String loginUserID,
											  @Valid @RequestBody NumberRange updateNumberRange, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		NumberRange updatedNumberRange = mastersService.updateNumberRange(numberRangeCode, loginUserID, updateNumberRange, authToken);
		return new ResponseEntity<>(updatedNumberRange , HttpStatus.OK);
	}

	@ApiOperation(response = NumberRange.class, value = "Delete NumberRange") // label for swagger
	@DeleteMapping("/numberRange/{numberRangeCode}")
	public ResponseEntity<?> deleteNumberRange(@PathVariable Long numberRangeCode, @RequestParam String loginUserID, @RequestParam String authToken) {
		mastersService.deleteNumberRange(numberRangeCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------User------------------------------------------------------------
	@ApiOperation(response = User.class, value = "Get all User details") // label for swagger
	@GetMapping("/login/users")
	public ResponseEntity<?> getAllUser(@RequestParam String authToken) {
		User[] userList = mastersService.getAllUser(authToken);
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}

	@ApiOperation(response = User.class, value = "Get a User") // label for swagger
	@GetMapping("/login/user/{id}")
	public ResponseEntity<?> getUser(@PathVariable String id, @RequestParam String authToken) {
		User dbUser = mastersService.getUser(id, authToken);
		return new ResponseEntity<>(dbUser, HttpStatus.OK);
	}

	@ApiOperation(response = User.class, value = "Create User") // label for swagger
	@PostMapping("/login/user")
	public ResponseEntity<?> postUser(@Valid @RequestBody AddUser newUser,
									  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		User createdUser = mastersService.createUser(newUser, loginUserID, authToken);
		return new ResponseEntity<>(createdUser, HttpStatus.OK);
	}

	@ApiOperation(response = User.class, value = "Update User") // label for swagger
	@PatchMapping("/login/user/{id}")
	public ResponseEntity<?> patchUser(@PathVariable String id,
									   @RequestParam String loginUserID,
									   @Valid @RequestBody UpdateUser updateUser,
									   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		User updatedUser = mastersService.updateUser(id, loginUserID,
				updateUser, authToken);
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	@ApiOperation(response = User.class, value = "Delete User") // label for swagger
	@DeleteMapping("/login/user/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable String id, @RequestParam String loginUserID,
										@RequestParam String authToken) {
		mastersService.deleteUser(id, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/**
	 * This end point will be called for Web App User Login
	 *
	 * @param userId
	 * @param password
	 * @return
	 */
	@ApiOperation(response = Optional.class, value = "Validate Login User") // label for swagger
	@RequestMapping(value = "/login/validate", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> loginUser(@RequestParam String userID, @RequestParam String password,
									   @RequestParam String authToken) {
		try {
			User loggedInUser = mastersService.validateUser(userID, password, authToken);
			log.info("LoginUser::: " + loggedInUser);
			return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
		} catch (BadRequestException e) {
			e.printStackTrace();
			CommonUtils commonUtils = new CommonUtils();
			Map<String, String> mapError = commonUtils.prepareErrorResponse(e.getLocalizedMessage());
			return new ResponseEntity<>(mapError, HttpStatus.UNAUTHORIZED);
		}
	}

}
