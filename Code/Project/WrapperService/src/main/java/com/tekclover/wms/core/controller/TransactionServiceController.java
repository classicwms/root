package com.tekclover.wms.core.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.tekclover.wms.core.batch.scheduler.BatchJobScheduler;
import com.tekclover.wms.core.model.masters.ImBasicData1;
import com.tekclover.wms.core.model.masters.SearchImBasicData1;
import com.tekclover.wms.core.model.transaction.*;
import com.tekclover.wms.core.model.warehouse.inbound.ASN;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.service.FileStorageService;
import com.tekclover.wms.core.service.ReportService;
import com.tekclover.wms.core.service.TransactionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wms-transaction-service")
@Api(tags = { "Transaction Service" }, value = "Transaction Service Operations") // label for swagger
@SwaggerDefinition(tags = { @Tag(name = "User", description = "Operations related to Transaction Modules") })
public class TransactionServiceController {

	@Autowired
	TransactionService transactionService;
	
	@Autowired
	ReportService reportService;
	
	@Autowired
    FileStorageService fileStorageService;
	
	@Autowired
	BatchJobScheduler batchJobScheduler;
	
	/*
     * Process the ASN Integraion data
     */
    @ApiOperation(response = PreInboundHeader.class, value = "Create ProcessASN") // label for swagger
   	@PostMapping("/preinboundheader/processInboundReceived")
   	public ResponseEntity<?> processASN(@RequestParam String authToken) 
   			throws IllegalAccessException, InvocationTargetException {
   		transactionService.processInboundReceived(authToken);
   		return new ResponseEntity<>(HttpStatus.OK);
   	}
	
	/*
	 * --------------------------------ContainerReceipt---------------------------------------------------------------
	 */
	@ApiOperation(response = ContainerReceipt.class, value = "Get all ContainerReceipt details") // label for swagger
	@GetMapping("/containerreceipt")
	public ResponseEntity<?> getContainerReceipts(@RequestParam String authToken) throws Exception {
		ContainerReceipt[] containerReceiptNoList = transactionService.getContainerReceipts(authToken);
		return new ResponseEntity<>(containerReceiptNoList, HttpStatus.OK);
	}

	@ApiOperation(response = ContainerReceipt.class, value = "Get a ContainerReceipt") // label for swagger 
	@GetMapping("/containerreceipt/{containerReceiptNo}")
	public ResponseEntity<?> getContainerReceipt(@PathVariable String containerReceiptNo,
												 @RequestParam String authToken) throws Exception{
    	ContainerReceipt containerreceipt = 
    			transactionService.getContainerReceipt(containerReceiptNo, authToken);
    	log.info("ContainerReceipt : " + containerreceipt);
		return new ResponseEntity<>(containerreceipt, HttpStatus.OK);
	}
	
	@ApiOperation(response = ContainerReceipt.class, value = "Search ContainerReceipt") // label for swagger
	@PostMapping("/containerreceipt/findContainerReceipt")
	public ContainerReceipt[] findContainerReceipt(@RequestBody SearchContainerReceipt searchContainerReceipt,
			@RequestParam String authToken) throws Exception {
		return transactionService.findContainerReceipt(searchContainerReceipt, authToken);
	}
	//Streaming
	@ApiOperation(response = ContainerReceipt.class, value = "Search ContainerReceipt New") // label for swagger
	@PostMapping("/containerreceipt/findContainerReceiptNew")
	public ContainerReceipt[] findContainerReceiptNew(@RequestBody SearchContainerReceipt searchContainerReceipt,
			@RequestParam String authToken) throws Exception {
		return transactionService.findContainerReceiptNew(searchContainerReceipt, authToken);
	}

	@ApiOperation(response = ContainerReceipt.class, value = "Create ContainerReceipt") // label for swagger
	@PostMapping("/containerreceipt")
	public ResponseEntity<?> postContainerReceipt(@Valid @RequestBody ContainerReceipt newContainerReceipt, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ContainerReceipt createdContainerReceipt = transactionService.createContainerReceipt(newContainerReceipt, loginUserID, authToken);
		return new ResponseEntity<>(createdContainerReceipt, HttpStatus.OK);
	}

	@ApiOperation(response = ContainerReceipt.class, value = "Update ContainerReceipt") // label for swagger
	@RequestMapping(value = "/containerreceipt/{containerReceiptNo}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchContainerReceipt(@PathVariable String containerReceiptNo, 
			@Valid @RequestBody ContainerReceipt updateContainerReceipt, @RequestParam String loginUserID, 
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ContainerReceipt updatedContainerReceipt = 
				transactionService.updateContainerReceipt(containerReceiptNo, loginUserID, updateContainerReceipt, authToken);
		return new ResponseEntity<>(updatedContainerReceipt, HttpStatus.OK);
	}

	@ApiOperation(response = ContainerReceipt.class, value = "Delete ContainerReceipt") // label for swagger
	@DeleteMapping("/containerreceipt/{containerReceiptNo}")
	public ResponseEntity<?> deleteContainerReceipt(@PathVariable String containerReceiptNo,
			 @RequestParam String warehouseId, @RequestParam String preInboundNo, @RequestParam String refDocNumber, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deleteContainerReceipt(preInboundNo, refDocNumber, containerReceiptNo, warehouseId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------Pre-InboundHeader---------------------------------------------------------------
	 */
	@ApiOperation(response = PreInboundHeader.class, value = "Get all PreInboundHeader details") // label for swagger
	@GetMapping("/preinboundheader")
	public ResponseEntity<?> getPreInboundHeaders(@RequestParam String authToken) throws Exception{
		PreInboundHeader[] preinboundheaderList = transactionService.getPreInboundHeaders(authToken);
		return new ResponseEntity<>(preinboundheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PreInboundHeader.class, value = "Get a PreInboundHeader") // label for swagger 
	@GetMapping("/preinboundheader/{preInboundNo}")
	public ResponseEntity<?> getPreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId,
			@RequestParam String authToken) throws Exception {
    	PreInboundHeader preinboundheader = transactionService.getPreInboundHeader(preInboundNo, warehouseId, authToken);
    	log.info("PreInboundHeader : " + preinboundheader);
		return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = PreInboundHeader.class, value = "Search PreInboundHeader") // label for swagger
	@PostMapping("/preinboundheader/findPreInboundHeader")
	public PreInboundHeader[] findPreInboundHeader(@RequestBody SearchPreInboundHeader searchPreInboundHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPreInboundHeader(searchPreInboundHeader, authToken);
	}

	//Stream
	@ApiOperation(response = PreInboundHeader.class, value = "Search PreInboundHeader New") // label for swagger
	@PostMapping("/preinboundheader/findPreInboundHeaderNew")
	public PreInboundHeader[] findPreInboundHeaderNew(@RequestBody SearchPreInboundHeader searchPreInboundHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPreInboundHeaderNew(searchPreInboundHeader, authToken);
	}
	
	@ApiOperation(response = PreInboundHeader.class, value = "Get a PreInboundHeader With Status=24") // label for swagger 
   	@GetMapping("/preinboundheader/{warehouseId}/inboundconfirm")
   	public ResponseEntity<?> getPreInboundHeader(@PathVariable String warehouseId,
													@RequestParam String authToken) throws Exception{
       	PreInboundHeader[] preinboundheader = 
       			transactionService.getPreInboundHeaderWithStatusId(warehouseId, authToken);
       	log.info("PreInboundHeader : " + preinboundheader);
   		return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
   	}

    @ApiOperation(response = PreInboundHeader.class, value = "Create PreInboundHeader") // label for swagger
	@PostMapping("/preinboundheader")
	public ResponseEntity<?> postPreInboundHeader(@Valid @RequestBody PreInboundHeader newPreInboundHeader, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		PreInboundHeader createdPreInboundHeader = transactionService.createPreInboundHeader(newPreInboundHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdPreInboundHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PreInboundHeader.class, value = "Update PreInboundHeader") // label for swagger
    @PatchMapping("/preinboundheader/{preInboundNo}")
	public ResponseEntity<?> patchPreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId,
			@Valid @RequestBody PreInboundHeader updatePreInboundHeader, @RequestParam String loginUserID, @RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
		PreInboundHeader createdPreInboundHeader = 
				transactionService.updatePreInboundHeader(preInboundNo, warehouseId, loginUserID, updatePreInboundHeader, authToken);
		return new ResponseEntity<>(createdPreInboundHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PreInboundHeader.class, value = "Delete PreInboundHeader") // label for swagger
	@DeleteMapping("/preinboundheader/{preInboundNo}")
	public ResponseEntity<?> deletePreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
    	transactionService.deletePreInboundHeader(preInboundNo, warehouseId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    @ApiOperation(response = PreInboundHeader.class, value = "Process ASN") // label for swagger
   	@PostMapping("/preinboundheader/processASN")
   	public ResponseEntity<?> processASN(@RequestBody List<PreInboundLine> newPreInboundLine, @RequestParam String loginUserID,
   			@RequestParam String authToken) 
   			throws IllegalAccessException, InvocationTargetException {
    	StagingHeader createdStagingHeader = transactionService.processASN(newPreInboundLine, loginUserID, authToken);
   		return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
   	}
    
    /*
     * --------------------------------Preinbound-Line---------------------------------------------------------------
     */
    @ApiOperation(response = PreInboundLine.class, value = "Insertion of BOM item") // label for swagger
   	@PostMapping("/preinboundline/bom")
   	public ResponseEntity<?> postPreInboundLineBOM(@RequestParam String preInboundNo, 
   			@RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String itemCode, 
   			@RequestParam Long lineNo, @RequestParam String loginUserID, @RequestParam String authToken) 
   			throws IllegalAccessException, InvocationTargetException {
    	PreInboundLine[] createdPreInboundLine = 
    			transactionService.createPreInboundLineBOM(preInboundNo, warehouseId, refDocNumber, itemCode, 
   						lineNo, loginUserID, authToken);
   		return new ResponseEntity<>(createdPreInboundLine , HttpStatus.OK);
   	}
    
    @ApiOperation(response = PreInboundLine.class, value = "Get a PreInboundLine") // label for swagger 
	@GetMapping("/preinboundline/{preInboundNo}")
	public ResponseEntity<?> getPreInboundLine(@PathVariable String preInboundNo, @RequestParam String authToken) throws Exception{
    	PreInboundLine[] preinboundline = transactionService.getPreInboundLine(preInboundNo, authToken);
    	log.info("PreInboundLine : " + preinboundline);
		return new ResponseEntity<>(preinboundline, HttpStatus.OK);
	}
    
	/*
	 * --------------------------------InboundHeader---------------------------------
	 */
	@ApiOperation(response = InboundHeader.class, value = "Get all InboundHeader details") // label for swagger
	@GetMapping("/inboundheader")
	public ResponseEntity<?> getInboundHeaders(@RequestParam String authToken) throws Exception{
		InboundHeader[] refDocNumberList = transactionService.getInboundHeaders(authToken);
		return new ResponseEntity<>(refDocNumberList, HttpStatus.OK);
	}

	@ApiOperation(response = InboundHeader.class, value = "Get a InboundHeader") // label for swagger
	@GetMapping("/inboundheader/{refDocNumber}")
	public ResponseEntity<?> getInboundHeader(@PathVariable String refDocNumber, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String authToken) throws Exception{
		InboundHeader dbInboundHeader = transactionService.getInboundHeader(warehouseId, refDocNumber, preInboundNo, authToken);
		log.info("InboundHeader : " + dbInboundHeader);
		return new ResponseEntity<>(dbInboundHeader, HttpStatus.OK);
	}
	
	@ApiOperation(response = InboundHeader.class, value = "Search InboundHeader") // label for swagger
	@PostMapping("/inboundheader/findInboundHeader")
	public InboundHeader[] findInboundHeader(@RequestBody SearchInboundHeader searchInboundHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findInboundHeader(searchInboundHeader, authToken);
	}

	//Stream
	@ApiOperation(response = InboundHeader.class, value = "Search InboundHeader New") // label for swagger
	@PostMapping("/inboundheader/findInboundHeaderNew")
	public InboundHeader[] findInboundHeaderNew(@RequestBody SearchInboundHeader searchInboundHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findInboundHeaderNew(searchInboundHeader, authToken);
	}
	
	@ApiOperation(response = InboundHeader.class, value = "Get a InboundHeader") // label for swagger 
   	@GetMapping("/inboundheader/inboundconfirm")
   	public ResponseEntity<?> getInboundHeader(@RequestParam String warehouseId, @RequestParam String authToken) throws Exception{
       	InboundHeaderEntity[] inboundheaderEntity = transactionService.getInboundHeaderWithStatusId(warehouseId, authToken);
       	log.info("PreInboundHeader : " + inboundheaderEntity);
   		return new ResponseEntity<>(inboundheaderEntity, HttpStatus.OK);
   	}

	@ApiOperation(response = InboundHeader.class, value = "Create InboundHeader") // label for swagger
	@PostMapping("/inboundheader")
	public ResponseEntity<?> postInboundHeader(@Valid @RequestBody InboundHeader newInboundHeader, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		InboundHeader createdInboundHeader = transactionService.createInboundHeader(newInboundHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdInboundHeader, HttpStatus.OK);
	}

	@ApiOperation(response = InboundHeader.class, value = "Update InboundHeader") // label for swagger
	@RequestMapping(value = "/inboundheader/{refDocNumber}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchInboundHeader(@PathVariable String refDocNumber, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String loginUserID, @RequestParam String authToken, 
			@Valid @RequestBody InboundHeader updateInboundHeader)
			throws IllegalAccessException, InvocationTargetException {
		InboundHeader updatedInboundHeader = 
				transactionService.updateInboundHeader(warehouseId, refDocNumber, preInboundNo, loginUserID, updateInboundHeader, authToken);
		return new ResponseEntity<>(updatedInboundHeader, HttpStatus.OK);
	}
	
    @ApiOperation(response = InboundHeader.class, value = "Replace ASN") // label for swagger
	@GetMapping("/inboundheader/replaceASN")
	public ResponseEntity<?> replaceASN(@RequestParam String refDocNumber, @RequestParam String preInboundNo, 
			@RequestParam String asnNumber, @RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
    	transactionService.replaceASN(refDocNumber, preInboundNo, asnNumber, authToken);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@ApiOperation(response = InboundHeader.class, value = "Inbound Header & Line Confirm") // label for swagger
    @PatchMapping("/inboundheader/confirmIndividual")
	public ResponseEntity<?> patchInboundHeaderConfirm(@RequestParam String warehouseId, @RequestParam String preInboundNo, 
			@RequestParam String refDocNumber, @RequestParam String loginUserID, @RequestParam String authToken) 
					throws IllegalAccessException, InvocationTargetException {
		AXApiResponse createdInboundHeaderResponse = 
				transactionService.updateInboundHeaderConfirm(warehouseId, preInboundNo, refDocNumber, loginUserID, authToken);
		return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
	}

	@ApiOperation(response = InboundHeader.class, value = "Delete InboundHeader") // label for swagger
	@DeleteMapping("/inboundheader/{refDocNumber}")
	public ResponseEntity<?> deleteInboundHeader(@PathVariable String refDocNumber, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deleteInboundHeader(warehouseId, refDocNumber, preInboundNo, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------InboundLine---------------------------------
	 */
	@ApiOperation(response = InboundLine.class, value = "Get all InboundLine details") // label for swagger
	@GetMapping("/inboundline")
	public ResponseEntity<?> getInboundLines(@RequestParam String authToken) throws Exception{
		InboundLine[] lineNoList = transactionService.getInboundLines(authToken);
		return new ResponseEntity<>(lineNoList, HttpStatus.OK);
	}

	@ApiOperation(response = InboundLine.class, value = "Get a InboundLine") // label for swagger
	@GetMapping("/inboundline/{lineNo}")
	public ResponseEntity<?> getInboundLine(@PathVariable Long lineNo, @RequestParam String languageId, @RequestParam String companyCodeId,
											@RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
											@RequestParam String preInboundNo, @RequestParam String itemCode,
											@RequestParam String authToken) throws Exception{
		InboundLine dbInboundLine = 
				transactionService.getInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, authToken);
		log.info("InboundLine : " + dbInboundLine);
		return new ResponseEntity<>(dbInboundLine, HttpStatus.OK);
	}

	@ApiOperation(response = InboundLine.class, value = "Search InboundLine") // label for swagger
	@PostMapping("/inboundline/findInboundLine")
	public InboundLine[] findInboundLine(@RequestBody SearchInboundLine searchInboundLine,
											 @RequestParam String authToken) throws Exception {
		return transactionService.findInboundLine(searchInboundLine, authToken);
	}

	@ApiOperation(response = InboundLine.class, value = "Create InboundLine") // label for swagger
	@PostMapping("/inboundline")
	public ResponseEntity<?> postInboundLine(@Valid @RequestBody InboundLine newInboundLine, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		InboundLine createdInboundLine = transactionService.createInboundLine(newInboundLine, loginUserID, authToken);
		return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
	}

	@ApiOperation(response = InboundLine.class, value = "Update InboundLine") // label for swagger
	@RequestMapping(value = "/inboundline", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchInboundLine(@RequestParam Long lineNo, @RequestParam String warehouseId, @RequestParam String refDocNumber, 
			@RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String loginUserID, 
			@RequestParam String authToken, @Valid @RequestBody InboundLine updateInboundLine)
			throws IllegalAccessException, InvocationTargetException {
		InboundLine updatedInboundLine = 
				transactionService.updateInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine, authToken);
		return new ResponseEntity<>(updatedInboundLine, HttpStatus.OK);
	}

	@ApiOperation(response = InboundLine.class, value = "Delete InboundLine") // label for swagger
	@DeleteMapping("/inboundline/{lineNo}")
	public ResponseEntity<?> deleteInboundLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deleteInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	// -------------------StagingHeader----------------------------------------------------------------------------------------------
	@ApiOperation(response = StagingHeader.class, value = "Get all StagingHeader details") // label for swagger
	@GetMapping("/stagingheader")
	public ResponseEntity<?> getStagingHeaders(@RequestParam String authToken) throws Exception{
		StagingHeader[] stagingheaderList = transactionService.getStagingHeaders(authToken);
		return new ResponseEntity<>(stagingheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StagingHeader.class, value = "Get a StagingHeader") // label for swagger 
	@GetMapping("/stagingheader/{stagingNo}")
	public ResponseEntity<?> getStagingHeader(@PathVariable String stagingNo, @RequestParam String languageId, 
			@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String authToken) throws Exception{
    	StagingHeader stagingheader = transactionService.getStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, authToken);
    	log.info("StagingHeader : " + stagingheader);
		return new ResponseEntity<>(stagingheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = StagingHeader.class, value = "Search StagingHeader") // label for swagger
	@PostMapping("/stagingheader/findStagingHeader")
	public StagingHeader[] findStagingHeader(@RequestBody SearchStagingHeader searchStagingHeader, @RequestParam String authToken)
			throws Exception {
		return transactionService.findStagingHeader(searchStagingHeader, authToken);
	}

	//Stream
	@ApiOperation(response = StagingHeader.class, value = "Search StagingHeader New") // label for swagger
	@PostMapping("/stagingheader/findStagingHeaderNew")
	public StagingHeader[] findStagingHeaderNew(@RequestBody SearchStagingHeader searchStagingHeader, @RequestParam String authToken)
			throws Exception {
		return transactionService.findStagingHeaderNew(searchStagingHeader, authToken);
	}

    @ApiOperation(response = StagingHeader.class, value = "Create StagingHeader") // label for swagger
	@PostMapping("/stagingheader")
	public ResponseEntity<?> postStagingHeader(@Valid @RequestBody StagingHeader newStagingHeader, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
		StagingHeader createdStagingHeader = transactionService.createStagingHeader(newStagingHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdStagingHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = StagingHeader.class, value = "Update StagingHeader") // label for swagger
    @PatchMapping("/stagingheader/{stagingNo}")
	public ResponseEntity<?> patchStagingHeader(@PathVariable String stagingNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber,
			@Valid @RequestBody StagingHeader updateStagingHeader, @RequestParam String loginUserID, @RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
		StagingHeader createdStagingHeader = 
				transactionService.updateStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, loginUserID, 
						updateStagingHeader, authToken);
		return new ResponseEntity<>(createdStagingHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = StagingHeader.class, value = "Delete StagingHeader") // label for swagger
	@DeleteMapping("/stagingheader/{stagingNo}")
	public ResponseEntity<?> deleteStagingHeader(@PathVariable String stagingNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String loginUserID,
			@RequestParam String authToken) {
    	transactionService.deleteStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    /*
     * Barccode Generation
     * ----------------------
     */
    @ApiOperation(response = StagingHeader.class, value = "Get Barcodes") // label for swagger 
	@GetMapping("/stagingheader/{numberOfCases}/barcode")
	public ResponseEntity<?> getStagingHeader(@PathVariable Long numberOfCases, @RequestParam String warehouseId,
			@RequestParam String authToken) {
    	String[] stagingheader = transactionService.generateNumberRanges (numberOfCases, warehouseId, authToken);
    	log.info("StagingHeader : " + stagingheader);
		return new ResponseEntity<>(stagingheader, HttpStatus.OK);
	}
	
	/*
	 * --------------------------------StagingLine-----------------------------------------------------------------------
	 */
	@ApiOperation(response = StagingLine.class, value = "Get all StagingLine details") // label for swagger
	@GetMapping("/stagingline")
	public ResponseEntity<?> getStagingLines(@RequestParam String authToken) throws Exception{
		StagingLineEntity[] palletCodeList = transactionService.getStagingLines(authToken);
		return new ResponseEntity<>(palletCodeList, HttpStatus.OK);
	}

	@ApiOperation(response = StagingLine.class, value = "Get a StagingLine") // label for swagger
	@GetMapping("/stagingline/{lineNo}")
	public ResponseEntity<?> getStagingLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String caseCode, @RequestParam String palletCode, @RequestParam String itemCode,
											@RequestParam String authToken) throws Exception{
		StagingLineEntity dbStagingLine = 
				transactionService.getStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode, caseCode, lineNo, itemCode, authToken);
		log.info("StagingLine : " + dbStagingLine);
		return new ResponseEntity<>(dbStagingLine, HttpStatus.OK);
	}
	
	@ApiOperation(response = StagingLine.class, value = "Search StagingLine") // label for swagger
	@PostMapping("/stagingline/findStagingLine")
	public StagingLineEntity[] findStagingLine(@RequestBody SearchStagingLine searchStagingLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findStagingLine(searchStagingLine, authToken);
	}

	@ApiOperation(response = StagingLine.class, value = "Create StagingLine") // label for swagger
	@PostMapping("/stagingline")
	public ResponseEntity<?> postStagingLine(@Valid @RequestBody List<StagingLine> newStagingLine, 
			@RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StagingLineEntity[] createdStagingLine = 
				transactionService.createStagingLine(newStagingLine, loginUserID, authToken);
		return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
	}

	@ApiOperation(response = StagingLine.class, value = "Update StagingLine") // label for swagger
	@RequestMapping(value = "/stagingline/{lineNo}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchStagingLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String caseCode, @RequestParam String palletCode, @RequestParam String itemCode, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody StagingLineEntity updateStagingLine)
			throws IllegalAccessException, InvocationTargetException {
		StagingLineEntity updatedStagingLine = 
				transactionService.updateStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode, caseCode, 
						lineNo, itemCode, loginUserID, updateStagingLine, authToken);
		return new ResponseEntity<>(updatedStagingLine, HttpStatus.OK);
	}
	
	@ApiOperation(response = StagingLine.class, value = "Update StagingLine") // label for swagger
	@PatchMapping("/stagingline/caseConfirmation")
	public ResponseEntity<?> patchStagingLineForCaseConfirmation(@RequestBody List<CaseConfirmation> caseConfirmations, 
			@RequestParam String caseCode, @RequestParam String loginUserID, @RequestParam String authToken) 
					throws IllegalAccessException, InvocationTargetException {
		StagingLineEntity[] createdStagingLine = 
				transactionService.caseConfirmation(caseConfirmations, caseCode, loginUserID, authToken);
		return new ResponseEntity<>(createdStagingLine , HttpStatus.OK);
	}

	@ApiOperation(response = StagingLine.class, value = "Delete StagingLine") // label for swagger
	@DeleteMapping("/stagingline/{lineNo}")
	public ResponseEntity<?> deleteStagingLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String caseCode, @RequestParam String palletCode, @RequestParam String itemCode, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deleteStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode, caseCode, lineNo, 
				itemCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
    @ApiOperation(response = StagingLine.class, value = "Delete StagingLine") // label for swagger
   	@DeleteMapping("/stagingline/{lineNo}/cases")
   	public ResponseEntity<?> deleteCases(@PathVariable Long lineNo, @RequestParam String preInboundNo, 
   			@RequestParam String caseCode, @RequestParam String itemCode, @RequestParam String loginUserID, 
   			@RequestParam String authToken) {
    	transactionService.deleteCases( preInboundNo, lineNo, itemCode, caseCode, loginUserID, authToken);
   		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
   	}
    
    @ApiOperation(response = StagingLine.class, value = "AssignHHTUser StagingLine") // label for swagger
    @PatchMapping("/stagingline/assignHHTUser")
	public ResponseEntity<?> assignHHTUser (@RequestBody List<AssignHHTUser> assignHHTUsers, @RequestParam String assignedUserId, 
			@RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, 
	InvocationTargetException {
    	log.info("hello");
		StagingLineEntity[] updatedStagingLine = 
				transactionService.assignHHTUser(assignHHTUsers, assignedUserId, loginUserID, authToken);
		return new ResponseEntity<>(updatedStagingLine , HttpStatus.OK);
	}

	/*
	 * --------------------------------GrHeader---------------------------------------------------------------------
	 */
	@ApiOperation(response = GrHeader.class, value = "Get all GrHeader details") // label for swagger
	@GetMapping("/grheader")
	public ResponseEntity<?> getGrHeaders(@RequestParam String authToken) throws Exception{
		GrHeader[] goodsReceiptNoList = transactionService.getGrHeaders(authToken);
		return new ResponseEntity<>(goodsReceiptNoList, HttpStatus.OK);
	}

	@ApiOperation(response = GrHeader.class, value = "Get a GrHeader") // label for swagger
	@GetMapping("/grheader/{goodsReceiptNo}")
	public ResponseEntity<?> getGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String authToken) throws Exception{
		GrHeader dbGrHeader = 
				transactionService.getGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, 
						caseCode, authToken);
		log.info("GrHeader : " + dbGrHeader);
		return new ResponseEntity<>(dbGrHeader, HttpStatus.OK);
	}
	
	@ApiOperation(response = GrHeader.class, value = "Search GrHeader") // label for swagger
	@PostMapping("/grheader/findGrHeader")
	public GrHeader[] findGrHeader(@RequestBody SearchGrHeader searchGrHeader, @RequestParam String authToken)
			throws Exception {
		return transactionService.findGrHeader(searchGrHeader, authToken);
	}
	//Stream - JPA
	@ApiOperation(response = GrHeader.class, value = "Search GrHeader New") // label for swagger
	@PostMapping("/grheader/findGrHeaderNew")
	public GrHeader[] findGrHeaderNew(@RequestBody SearchGrHeader searchGrHeader, @RequestParam String authToken)
			throws Exception {
		return transactionService.findGrHeaderNew(searchGrHeader, authToken);
	}

	@ApiOperation(response = GrHeader.class, value = "Create GrHeader") // label for swagger
	@PostMapping("/grheader")
	public ResponseEntity<?> postGrHeader(@Valid @RequestBody GrHeader newGrHeader, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		GrHeader createdGrHeader = transactionService.createGrHeader(newGrHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
	}

	@ApiOperation(response = GrHeader.class, value = "Update GrHeader") // label for swagger
	@RequestMapping(value = "/grheader/{goodsReceiptNo}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchGrHeader(@RequestParam String goodsReceiptNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID, 
			@RequestParam String authToken, @Valid @RequestBody GrHeader updateGrHeader)
			throws IllegalAccessException, InvocationTargetException {
		GrHeader updatedGrHeader = 
				transactionService.updateGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, 
						palletCode, caseCode, loginUserID, updateGrHeader, authToken);
		return new ResponseEntity<>(updatedGrHeader, HttpStatus.OK);
	}

	@ApiOperation(response = GrHeader.class, value = "Delete GrHeader") // label for swagger
	@DeleteMapping("/grheader/{goodsReceiptNo}")
	public ResponseEntity<?> deleteGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID, 
			@RequestParam String authToken) {
		transactionService.deleteGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------GrLine------------------------------------------------------------------------------
	 */
	@ApiOperation(response = GrLine.class, value = "Get all GrLine details") // label for swagger
	@GetMapping("/grline")
	public ResponseEntity<?> getGrLines(@RequestParam String authToken) throws Exception{
		GrLine[] itemCodeList = transactionService.getGrLines(authToken);
		return new ResponseEntity<>(itemCodeList, HttpStatus.OK);
	}

	@ApiOperation(response = GrLine.class, value = "Get a GrLine") // label for swagger
	@GetMapping("/grline/{lineNo}")
	public ResponseEntity<?> getGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes, 
			@RequestParam String itemCode, @RequestParam String authToken) throws Exception{
		GrLine dbGrLine = 
				transactionService.getGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, 
						packBarcodes, lineNo, itemCode, authToken);
		log.info("GrLine : " + dbGrLine);
		return new ResponseEntity<>(dbGrLine, HttpStatus.OK);
	}
	
	// PRE_IB_NO/REF_DOC_NO/PACK_BARCODE/IB_LINE_NO/ITM_CODE
    @ApiOperation(response = GrLine.class, value = "Get a GrLine") // label for swagger 
	@GetMapping("/grline/{lineNo}/putawayline")
	public ResponseEntity<?> getGrLine(@PathVariable Long lineNo, @RequestParam String preInboundNo, 
			@RequestParam String refDocNumber, @RequestParam String packBarcodes, @RequestParam String itemCode,
			@RequestParam String authToken) throws Exception{
    	GrLine[] grline = transactionService.getGrLine(preInboundNo, refDocNumber, packBarcodes, lineNo, itemCode, authToken);
    	log.info("GrLine : " + grline);
		return new ResponseEntity<>(grline, HttpStatus.OK);
	}
    
    @ApiOperation(response = GrLine.class, value = "Search GrLine") // label for swagger
	@PostMapping("/findGrLine")
	public GrLine[] findGrLine(@RequestBody SearchGrLine searchGrLine, @RequestParam String authToken)
			throws Exception {
		return transactionService.findGrLine(searchGrLine, authToken);
	}

	@ApiOperation(response = GrLine.class, value = "Create GrLine") // label for swagger
	@PostMapping("/grline")
	public ResponseEntity<?> postGrLine(@Valid @RequestBody List<AddGrLine> newGrLine, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		GrLine[] createdGrLine = transactionService.createGrLine(newGrLine, loginUserID, authToken);
		return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
	}

	@ApiOperation(response = GrLine.class, value = "Update GrLine") // label for swagger
	@RequestMapping(value = "/grline", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchGrLine(@RequestParam Long lineNo, @RequestParam String warehouseId, @RequestParam String preInboundNo, 
			@RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode, 
			@RequestParam String caseCode, @RequestParam String packBarcodes, @RequestParam String itemCode, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody GrLine updateGrLine)
			throws IllegalAccessException, InvocationTargetException {
		GrLine updatedGrLine = 
				transactionService.updateGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, 
						packBarcodes, lineNo, itemCode, loginUserID, updateGrLine, authToken);
		return new ResponseEntity<>(updatedGrLine, HttpStatus.OK);
	}

	@ApiOperation(response = GrLine.class, value = "Delete GrLine") // label for swagger
	@DeleteMapping("/grline/{lineNo}")
	public ResponseEntity<?> deleteGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, 
			@RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes, 
			@RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deleteGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, lineNo, itemCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//-----------------PACK_BARCODE-GENERATION----------------------------------------------------------------------------
    @ApiOperation(response = GrLine.class, value = "Get PackBarcodes") // label for swagger 
	@GetMapping("/grline/packBarcode")
	public ResponseEntity<?> getPackBarcode(@RequestParam Long acceptQty, @RequestParam Long damageQty, 
			@RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) throws Exception{
    	PackBarcode[] packBarcodes = 
    			transactionService.generatePackBarcode (acceptQty, damageQty, warehouseId, loginUserID, authToken);
    	log.info("packBarcodes : " + packBarcodes);
		return new ResponseEntity<>(packBarcodes, HttpStatus.OK);
	}
    
    /*
	 * --------------------------------PutAwayHeader---------------------------------
	 */
	@ApiOperation(response = PutAwayHeader.class, value = "Get all PutAwayHeader details") // label for swagger
	@GetMapping("/putawayheader")
	public ResponseEntity<?> getPutAwayHeaders(@RequestParam String authToken) throws Exception{
		PutAwayHeader[] putAwayNumberList = transactionService.getPutAwayHeaders(authToken);
		return new ResponseEntity<>(putAwayNumberList, HttpStatus.OK);
	}

	@ApiOperation(response = PutAwayHeader.class, value = "Get a PutAwayHeader") // label for swagger
	@GetMapping("/putawayheader/{putAwayNumber}")
	public ResponseEntity<?> getPutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, @RequestParam String preInboundNo, 
			@RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode, @RequestParam String caseCode, 
			@RequestParam String packBarcodes, @RequestParam String proposedStorageBin, @RequestParam String authToken) throws Exception{
		PutAwayHeader dbPutAwayHeader = transactionService.getPutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin, authToken);
		log.info("PutAwayHeader : " + dbPutAwayHeader);
		return new ResponseEntity<>(dbPutAwayHeader, HttpStatus.OK);
	}
	
	@ApiOperation(response = PutAwayHeader.class, value = "Get a PutAwayHeader") // label for swagger 
	@GetMapping("/putawayheader/{refDocNumber}/inboundreversal/asn")
	public ResponseEntity<?> getPutAwayHeaderForASN(@PathVariable String refDocNumber, @RequestParam String authToken) throws Exception{
    	PutAwayHeader[] putawayheader = transactionService.getPutAwayHeader(refDocNumber, authToken);
    	log.info("PutAwayHeader : " + putawayheader);
		return new ResponseEntity<>(putawayheader, HttpStatus.OK);
	}
	
	@ApiOperation(response = PutAwayHeader.class, value = "Search PutAwayHeader") // label for swagger
	@PostMapping("/putawayheader/findPutAwayHeader")
	public PutAwayHeader[] findPutAwayHeader(@RequestBody SearchPutAwayHeader searchPutAwayHeader, @RequestParam String authToken)
			throws Exception {
		return transactionService.findPutAwayHeader(searchPutAwayHeader, authToken);
	}
	//Stream
	@ApiOperation(response = PutAwayHeader.class, value = "Search PutAwayHeader New") // label for swagger
	@PostMapping("/putawayheader/findPutAwayHeaderNew")
	public PutAwayHeader[] findPutAwayHeaderNew(@RequestBody SearchPutAwayHeader searchPutAwayHeader, @RequestParam String authToken)
			throws Exception {
		return transactionService.findPutAwayHeaderNew(searchPutAwayHeader, authToken);
	}

	@ApiOperation(response = PutAwayHeader.class, value = "Create PutAwayHeader") // label for swagger
	@PostMapping("/putawayheader")
	public ResponseEntity<?> postPutAwayHeader(@Valid @RequestBody PutAwayHeader newPutAwayHeader, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		PutAwayHeader createdPutAwayHeader = transactionService.createPutAwayHeader(newPutAwayHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdPutAwayHeader, HttpStatus.OK);
	}

	@ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader") // label for swagger
	@RequestMapping(value = "/putawayheader", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchPutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, @RequestParam String preInboundNo, @RequestParam String refDocNumber, 
			@RequestParam String goodsReceiptNo, @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes, 
			@RequestParam String proposedStorageBin, @Valid @RequestBody PutAwayHeader updatePutAwayHeader, @RequestParam String loginUserID, 
			@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PutAwayHeader updatedPutAwayHeader = 
				transactionService.updatePutAwayHeader(warehouseId, preInboundNo, 
						refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin, updatePutAwayHeader, loginUserID, authToken);
		return new ResponseEntity<>(updatedPutAwayHeader, HttpStatus.OK);
	}
	
	@ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader") // label for swagger
    @PatchMapping("/putawayheader/{refDocNumber}/reverse")
	public ResponseEntity<?> patchPutAwayHeader(@PathVariable String refDocNumber, @RequestParam String packBarcodes, 
			@RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		transactionService.updatePutAwayHeader(refDocNumber, packBarcodes, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(response = PutAwayHeader.class, value = "Delete PutAwayHeader") // label for swagger
	@DeleteMapping("/putawayheader/{putAwayNumber}")
	public ResponseEntity<?> deletePutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, @RequestParam String preInboundNo, 
			@RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode, @RequestParam String caseCode, 
			@RequestParam String packBarcodes, @RequestParam String proposedStorageBin, @RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deletePutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, putAwayNumber, 
				proposedStorageBin, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------PutAwayLine---------------------------------
	 */
	@ApiOperation(response = PutAwayLine.class, value = "Get all PutAwayLine details") // label for swagger
	@GetMapping("/putawayline/confirmedStorageBin")
	public ResponseEntity<?> getPutAwayLines(@RequestParam String authToken) throws Exception{
		PutAwayLine[] confirmedStorageBinList = transactionService.getPutAwayLines(authToken);
		return new ResponseEntity<>(confirmedStorageBinList, HttpStatus.OK);
	}

	@ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger
	@GetMapping("/putawayline/{confirmedStorageBin}")
	public ResponseEntity<?> getPutAwayLine(@PathVariable String confirmedStorageBin, @RequestParam String warehouseId, @RequestParam String goodsReceiptNo, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String putAwayNumber, @RequestParam Long lineNo, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String authToken) throws Exception{
		PutAwayLine dbPutAwayLine = transactionService.getPutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode, 
				proposedStorageBin, confirmedStorageBin, authToken);
		log.info("PutAwayLine : " + dbPutAwayLine);
		return new ResponseEntity<>(dbPutAwayLine, HttpStatus.OK);
	}
	
	@ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger 
	@GetMapping("/putawayline/{refDocNumber}/inboundreversal/palletId")
	public ResponseEntity<?> getPutAwayLineForInboundLine(@PathVariable String refDocNumber,
														  @RequestParam String authToken) throws Exception{
    	PutAwayLine[] putawayline = transactionService.getPutAwayLine(refDocNumber, authToken);
    	log.info("PutAwayLine : " + putawayline);
		return new ResponseEntity<>(putawayline, HttpStatus.OK);
	}
	
	@ApiOperation(response = PutAwayLine.class, value = "Search PutAwayLine") // label for swagger
	@PostMapping("/putawayline/findPutAwayLine")
	public PutAwayLine[] findPutAwayLine(@RequestBody SearchPutAwayLine searchPutAwayLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPutAwayLine(searchPutAwayLine, authToken);
	}

	@ApiOperation(response = PutAwayLine.class, value = "Create PutAwayLine") // label for swagger
	@PostMapping("/putawayline")
	public ResponseEntity<?> postPutAwayLine(@Valid @RequestBody List<AddPutAwayLine> newPutAwayLine, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		PutAwayLine[] createdPutAwayLine = transactionService.createPutAwayLine(newPutAwayLine, loginUserID, authToken);
		return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
	}

	@ApiOperation(response = PutAwayLine.class, value = "Update PutAwayLine") // label for swagger
	@RequestMapping(value = "/putawayline", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchPutAwayLine(@PathVariable String confirmedStorageBin, @RequestParam String warehouseId, @RequestParam String goodsReceiptNo, 
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String putAwayNumber, @RequestParam Long lineNo, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, @Valid @RequestBody PutAwayLine updatePutAwayLine, 
			@RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PutAwayLine updatedPutAwayLine = 
				transactionService.updatePutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode, 
						proposedStorageBin, confirmedStorageBin, updatePutAwayLine, loginUserID, authToken);
		return new ResponseEntity<>(updatedPutAwayLine, HttpStatus.OK);
	}

	@ApiOperation(response = PutAwayLine.class, value = "Delete PutAwayLine") // label for swagger
	@DeleteMapping("/putawayline/{confirmedStorageBin}")
	public ResponseEntity<?> deletePutAwayLine(@PathVariable String confirmedStorageBin,@RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String goodsReceiptNo,
			@RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String putAwayNumber, @RequestParam Long lineNo, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deletePutAwayLine(languageId,companyCodeId,plantId,warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode, proposedStorageBin,
				confirmedStorageBin, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------InventoryMovement---------------------------------
	 */
	@ApiOperation(response = InventoryMovement.class, value = "Get all InventoryMovement details") // label for swagger
	@GetMapping("/inventorymovement")
	public ResponseEntity<?> getInventoryMovements(@RequestParam String authToken) throws Exception{
		InventoryMovement[] movementTypeList = transactionService.getInventoryMovements(authToken);
		return new ResponseEntity<>(movementTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = InventoryMovement.class, value = "Get a InventoryMovement") // label for swagger
	@GetMapping("/inventorymovement/{movementType}")
	public ResponseEntity<?> getInventoryMovement(@PathVariable Long movementType, @RequestParam String warehouseId, @RequestParam Long submovementType, 
			@RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String batchSerialNumber, @RequestParam String movementDocumentNo, 
			@RequestParam String authToken) throws Exception{
		InventoryMovement dbInventoryMovement = transactionService.getInventoryMovement(warehouseId, movementType, submovementType, 
				packBarcodes, itemCode, batchSerialNumber, movementDocumentNo, authToken);
		log.info("InventoryMovement : " + dbInventoryMovement);
		return new ResponseEntity<>(dbInventoryMovement, HttpStatus.OK);
	}
	
	@ApiOperation(response = InventoryMovement.class, value = "Search InventoryMovement") // label for swagger
	@PostMapping("/inventorymovement/findInventoryMovement")
	public InventoryMovement[] findInventoryMovement(@RequestBody SearchInventoryMovement searchInventoryMovement,
			@RequestParam String authToken) throws Exception {
		return transactionService.findInventoryMovement(searchInventoryMovement, authToken);
	}

	@ApiOperation(response = InventoryMovement.class, value = "Create InventoryMovement") // label for swagger
	@PostMapping("/inventorymovement")
	public ResponseEntity<?> postInventoryMovement(@Valid @RequestBody InventoryMovement newInventoryMovement, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		InventoryMovement createdInventoryMovement = transactionService.createInventoryMovement(newInventoryMovement, loginUserID, authToken);
		return new ResponseEntity<>(createdInventoryMovement, HttpStatus.OK);
	}

	@ApiOperation(response = InventoryMovement.class, value = "Update InventoryMovement") // label for swagger
	@RequestMapping(value = "/inventorymovement/{movementType}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchInventoryMovement(@PathVariable Long movementType, @RequestParam String languageId, 
			@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam Long submovementType, 
			@RequestParam String packBarcodes, @RequestParam String itemCode,@RequestParam String batchSerialNumber, @RequestParam String movementDocumentNo, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody InventoryMovement updateInventoryMovement)
			throws IllegalAccessException, InvocationTargetException {
		InventoryMovement updatedInventoryMovement = 
				transactionService.updateInventoryMovement(warehouseId, movementType, submovementType, packBarcodes, itemCode, batchSerialNumber, movementDocumentNo, updateInventoryMovement, 
						loginUserID, authToken);
		return new ResponseEntity<>(updatedInventoryMovement, HttpStatus.OK);
	}

	@ApiOperation(response = InventoryMovement.class, value = "Delete InventoryMovement") // label for swagger
	@DeleteMapping("/inventorymovement/{movementType}")
	public ResponseEntity<?> deleteInventoryMovement(@PathVariable Long movementType, @RequestParam String warehouseId, @RequestParam Long submovementType, 
			@RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String batchSerialNumber, @RequestParam String movementDocumentNo, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deleteInventoryMovement(warehouseId, movementType, submovementType, packBarcodes, itemCode, batchSerialNumber, movementDocumentNo, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------Inventory---------------------------------
	 */
	@ApiOperation(response = Inventory.class, value = "Get all Inventory details") // label for swagger
	@GetMapping("/inventory")
	public ResponseEntity<?> getInventorys(@RequestParam String authToken) throws Exception{
		Inventory[] stockTypeIdList = transactionService.getInventorys(authToken);
		return new ResponseEntity<>(stockTypeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = Inventory.class, value = "Get a Inventory") // label for swagger
	@GetMapping("/inventory/{stockTypeId}")
	public ResponseEntity<?> getInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, 
			@RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String storageBin, 
			@RequestParam Long specialStockIndicatorId, @RequestParam String authToken) throws Exception{
		Inventory dbInventory = 
				transactionService.getInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId, authToken);
		log.info("Inventory : " + dbInventory);
		return new ResponseEntity<>(dbInventory, HttpStatus.OK);
	}
	
	@ApiOperation(response = Inventory.class, value = "Get a Inventory For Transfer") // label for swagger 
	@GetMapping("/inventory/transfer")
	public ResponseEntity<?> getInventory(@RequestParam String warehouseId, @RequestParam String packBarcodes, 
			@RequestParam String itemCode, @RequestParam String storageBin, @RequestParam String authToken) throws Exception{
    	Inventory inventory = 
    			transactionService.getInventory(warehouseId, packBarcodes, itemCode, storageBin, authToken);
    	log.info("Inventory : " + inventory);
		return new ResponseEntity<>(inventory, HttpStatus.OK);
	}
	
	@ApiOperation(response = Inventory.class, value = "Search Inventory") // label for swagger
	@PostMapping("/inventory/findInventory")
	public Inventory[] findInventory(@RequestBody SearchInventory searchInventory,
			@RequestParam String authToken) throws Exception {
		return transactionService.findInventory(searchInventory, authToken);
	}
	//SQL Query - New
	@ApiOperation(response = Inventory.class, value = "Search Inventory New") // label for swagger
	@PostMapping("/inventory/findInventoryNew")
	public Inventory[] findInventoryNew(@RequestBody SearchInventory searchInventory,
			@RequestParam String authToken) throws Exception {
		return transactionService.findInventoryNew(searchInventory, authToken);
	}

	@ApiOperation(response = Inventory.class, value = "Search Inventory by quantity validation") // label for swagger
	@PostMapping("/get-all-validated-inventory")
	public Inventory[] getQuantityValidatedInventory(@RequestBody SearchInventory searchInventory,
									 @RequestParam String authToken) throws Exception {
		return transactionService.getQuantityValidatedInventory(searchInventory, authToken);
	}

	
	
	@ApiOperation(response = Inventory.class, value = "Search Inventory") // label for swagger
	@PostMapping("/inventory/findInventory/pagination")
	public Page<Inventory> findInventory(@RequestBody SearchInventory searchInventory,
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "itemCode") String sortBy,
			@RequestParam String authToken) throws Exception {
		return transactionService.findInventory(searchInventory, pageNo, pageSize, sortBy, authToken);
	}

	@ApiOperation(response = Inventory.class, value = "Create Inventory") // label for swagger
	@PostMapping("/inventory")
	public ResponseEntity<?> postInventory(@Valid @RequestBody Inventory newInventory, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		Inventory createdInventory = transactionService.createInventory(newInventory, loginUserID, authToken);
		return new ResponseEntity<>(createdInventory, HttpStatus.OK);
	}

	@ApiOperation(response = Inventory.class, value = "Update Inventory") // label for swagger
	@RequestMapping(value = "/inventory/{stockTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, @RequestParam String packBarcodes, 
			@RequestParam String itemCode, @RequestParam String storageBin, @RequestParam Long specialStockIndicatorId, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody Inventory updateInventory)
			throws IllegalAccessException, InvocationTargetException {
		Inventory updatedInventory = 
				transactionService.updateInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId, updateInventory,
						loginUserID, authToken);
		return new ResponseEntity<>(updatedInventory, HttpStatus.OK);
	}

	@ApiOperation(response = Inventory.class, value = "Delete Inventory") // label for swagger
	@DeleteMapping("/inventory/{stockTypeId}")
	public ResponseEntity<?> deleteInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, @RequestParam String packBarcodes, 
			@RequestParam String itemCode, @RequestParam String storageBin, @RequestParam Long specialStockIndicatorId, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deleteInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------InHouseTransferHeader---------------------------------
	 */
	@ApiOperation(response = InhouseTransferHeader.class, value = "Get all InHouseTransferHeader details") // label for swagger
	@GetMapping("/inhousetransferheader")
	public ResponseEntity<?> getInHouseTransferHeaders(@RequestParam String authToken) throws Exception{
		InhouseTransferHeader[] transferNumberList = transactionService.getInhouseTransferHeaders(authToken);
		return new ResponseEntity<>(transferNumberList, HttpStatus.OK);
	}

	@ApiOperation(response = InhouseTransferHeader.class, value = "Get a InHouseTransferHeader") // label for swagger
	@GetMapping("/inhousetransferheader/{transferNumber}")
	public ResponseEntity<?> getInHouseTransferHeader(@PathVariable String transferNumber, @RequestParam String warehouseId, 
			@RequestParam Long transferTypeId, @RequestParam String authToken) throws Exception{
		InhouseTransferHeader dbInHouseTransferHeader = 
				transactionService.getInhouseTransferHeader(warehouseId, transferNumber, transferTypeId, authToken);
		log.info("InHouseTransferHeader : " + dbInHouseTransferHeader);
		return new ResponseEntity<>(dbInHouseTransferHeader, HttpStatus.OK);
	}
	
	@ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader") // label for swagger
	@PostMapping("/inhousetransferheader/findInHouseTransferHeader")
	public InhouseTransferHeader[] findInHouseTransferHeader(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findInHouseTransferHeader(searchInHouseTransferHeader, authToken);
	}

	//Stream
	@ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader New") // label for swagger
	@PostMapping("/inhousetransferheader/findInHouseTransferHeaderNew")
	public InhouseTransferHeader[] findInHouseTransferHeaderNew(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findInHouseTransferHeaderNew(searchInHouseTransferHeader, authToken);
	}

	@ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader") // label for swagger
	@PostMapping("/inhousetransferheader")
	public ResponseEntity<?> postInHouseTransferHeader(@Valid @RequestBody InhouseTransferHeader newInHouseTransferHeader, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		InhouseTransferHeader createdInHouseTransferHeader = transactionService.createInhouseTransferHeader(newInHouseTransferHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdInHouseTransferHeader, HttpStatus.OK);
	}
	
	/*
	 * --------------------------------InHouseTransferLine---------------------------------
	 */
	@ApiOperation(response = InhouseTransferLine.class, value = "Get all InHouseTransferLine details") // label for swagger
	@GetMapping("/inhousetransferline")
	public ResponseEntity<?> getInHouseTransferLines(@RequestParam String authToken) throws Exception{
		InhouseTransferLine[] transferNumberList = transactionService.getInhouseTransferLines(authToken);
		return new ResponseEntity<>(transferNumberList, HttpStatus.OK);
	}

	@ApiOperation(response = InhouseTransferLine.class, value = "Get a InHouseTransferLine") // label for swagger
	@GetMapping("/inhousetransferline/{transferNumber}")
	public ResponseEntity<?> getInHouseTransferLine(@PathVariable String transferNumber, @RequestParam String warehouseId, 
			@RequestParam String sourceItemCode, @RequestParam String authToken) throws Exception{
		InhouseTransferLine dbInHouseTransferLine = 
				transactionService.getInhouseTransferLine(warehouseId, transferNumber, sourceItemCode, authToken);
		log.info("InHouseTransferLine : " + dbInHouseTransferLine);
		return new ResponseEntity<>(dbInHouseTransferLine, HttpStatus.OK);
	}

	@ApiOperation(response = InhouseTransferLine.class, value = "Search InhouseTransferLine") // label for swagger
	@PostMapping("/inhousetransferline/findInhouseTransferLine")
	public InhouseTransferLine[] findInhouseTransferLine(@RequestBody SearchInhouseTransferLine searchInhouseTransferLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findInhouseTransferLine(searchInhouseTransferLine, authToken);
	}
	
	@ApiOperation(response = InhouseTransferLine.class, value = "Create InHouseTransferLine") // label for swagger
	@PostMapping("/inhousetransferline")
	public ResponseEntity<?> postInHouseTransferLine(@Valid @RequestBody InhouseTransferLine newInHouseTransferLine, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		InhouseTransferLine createdInHouseTransferLine = transactionService.createInhouseTransferLine(newInHouseTransferLine, loginUserID, authToken);
		return new ResponseEntity<>(createdInHouseTransferLine, HttpStatus.OK);
	}

	/*
	 * --------------------------------PreOutboundHeader---------------------------------
	 */
	@ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader") // label for swagger
	@PostMapping("/preoutboundheader/findPreOutboundHeader")
	public PreOutboundHeader[] findPreOutboundHeader(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPreOutboundHeader(searchPreOutboundHeader, authToken);
	}
	//Stream
	@ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader New") // label for swagger
	@PostMapping("/preoutboundheader/findPreOutboundHeaderNew")
	public PreOutboundHeader[] findPreOutboundHeaderNew(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPreOutboundHeaderNew(searchPreOutboundHeader, authToken);
	}

	//Stream
	@ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader New") // label for swagger
	@PostMapping("/preoutboundheader/findPreOutboundHeaderExcel")
	public PreOutboundHeader[] findPreOutboundHeaderNewExcel(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader,
														@RequestParam String authToken) throws Exception {

		SearchPreOutboundHeader searchPreOutboundHeader1 = new SearchPreOutboundHeader();
		searchPreOutboundHeader1.setWarehouseId(searchPreOutboundHeader.getWarehouseId());

		Date fromDate = null;
		Date toDate = null;

		try {
			if (searchPreOutboundHeader.getStStartDate().indexOf("T") > 0) {
				fromDate = DateUtils.convertStringToDateWithT(searchPreOutboundHeader.getStStartDate());
				toDate = DateUtils.convertStringToDateWithT(searchPreOutboundHeader.getStEndDate());
			} else {
				fromDate = DateUtils.addTimeToDate(searchPreOutboundHeader.getStStartDate(), 14, 0, 0);
				toDate = DateUtils.addTimeToDate(searchPreOutboundHeader.getStEndDate(), 13, 59, 59);
			}
			log.info("Date: " + fromDate + "," + toDate);

		} catch (Exception e) {
			throw new BadRequestException("Date should be in yyyy-MM-dd format.");
		}

		searchPreOutboundHeader1.setStartOrderDate(fromDate);
		searchPreOutboundHeader1.setEndOrderDate(toDate);


		return transactionService.findPreOutboundHeaderNew(searchPreOutboundHeader1, authToken);
	}

	//Patch
	@ApiOperation(response = PreOutboundHeader.class, value = "Update PreOutboundHeader") // label for swagger
	@PatchMapping("/preoutboundheader/{preOutboundNo}")
	public ResponseEntity<?> patchPreOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String warehouseId, @RequestParam String refDocNumber,
													@RequestParam String partnerCode, @Valid @RequestBody PreOutboundHeader updateOutboundHeader,
													@RequestParam String loginUserID, @RequestParam String authToken) {
		PreOutboundHeader createdPreOutboundHeader =
				transactionService.updatePreOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
						updateOutboundHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdPreOutboundHeader , HttpStatus.OK);
	}
	//Delete
	@ApiOperation(response = PreOutboundHeader.class, value = "Delete PreOutboundHeader") // label for swagger
	@DeleteMapping("/preoutboundheader/{preOutboundNo}")
	public ResponseEntity<?> deletePreOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String warehouseId, @RequestParam String refDocNumber,
													 @RequestParam String partnerCode, @RequestParam String loginUserID, @RequestParam String authToken) {
		transactionService.deletePreOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * -------------------PreOutboundLine---------------------------------------------------
	 */
	@ApiOperation(response = PreOutboundLine.class, value = "Search PreOutboundLine") // label for swagger
	@PostMapping("/preoutboundline/findPreOutboundLine")
	public PreOutboundLine[] findPreOutboundLine(@RequestBody SearchPreOutboundLine searchPreOutboundLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPreOutboundLine(searchPreOutboundLine, authToken);
	}
	
	/*
	 * --------------------------------OrderMangementLine---------------------------------
	 */	
	@ApiOperation(response = OrderManagementLine.class, value = "Search OrderMangementLine") // label for swagger
	@PostMapping("/ordermanagementline/findOrderManagementLine")
	public OrderManagementLine[] findOrderManagementLine(@RequestBody SearchOrderManagementLine searchOrderMangementLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findOrderManagementLine(searchOrderMangementLine, authToken);
	}

	//Streaming
	@ApiOperation(response = OrderManagementLine.class, value = "Search OrderMangementLine New") // label for swagger
	@PostMapping("/ordermanagementline/findOrderManagementLineNew")
	public OrderManagementLine[] findOrderManagementLineNew(@RequestBody SearchOrderManagementLine searchOrderMangementLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findOrderManagementLineNew(searchOrderMangementLine, authToken);
	}

	//Streaming - V2 [Limited Fields]
	@ApiOperation(response = OrderManagementLineV2.class, value = "Search OrderMangementLine V2 Limited Fields") // label for swagger
	@PostMapping("/ordermanagementline/findOrderManagementLineV2")
	public OrderManagementLineV2[] findOrderManagementLineV2(@RequestBody SearchOrderManagementLine searchOrderMangementLine, @RequestParam String authToken) throws Exception {
		return transactionService.findOrderManagementLineV2(searchOrderMangementLine, authToken);
	}
	
	@ApiOperation(response = OrderManagementLine.class, value = "UnAllocate") // label for swagger
    @PatchMapping("/ordermanagementline/unallocate")
	public ResponseEntity<?> patchUnallocate(@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackBarCode, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
					throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine updatedOrderManagementLine = 
				transactionService.doUnAllocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
						itemCode, proposedStorageBin, proposedPackBarCode, loginUserID, authToken);
		return new ResponseEntity<>(updatedOrderManagementLine , HttpStatus.OK);
	}
	
	@ApiOperation(response = OrderManagementLine.class, value = "Allocate") // label for swagger
    @PatchMapping("/ordermanagementline/allocate")
	public ResponseEntity<?> patchAllocate(@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber, 
			@RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) 
					throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine updatedOrderManagementLine = 
				transactionService.allocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
						itemCode, loginUserID, authToken);
		return new ResponseEntity<>(updatedOrderManagementLine , HttpStatus.OK);
	}
	
	@ApiOperation(response = OrderManagementLine.class, value = "Assign Picker") // label for swagger
    @PatchMapping("/ordermanagementline/assignPicker")
	public ResponseEntity<?> assignPicker(@RequestBody List<AssignPicker> assignPicker, 
			@RequestParam String assignedPickerId, @RequestParam String loginUserID, @RequestParam String authToken) 
					throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine[] updatedOrderManagementLine = 
				transactionService.doAssignPicker(assignPicker, assignedPickerId, loginUserID, authToken);
		return new ResponseEntity<>(updatedOrderManagementLine , HttpStatus.OK);
	}

	@ApiOperation(response = OrderManagementLine.class, value = "Allocate Patch") // label for swagger
	@PatchMapping("/ordermanagementline/allocate/patch")
	public ResponseEntity<?> patchAllocateV2(@Valid @RequestBody List<OrderManagementLine> orderManagementLineV2,
											   @RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine[] updatedOrderManagementLine =
				transactionService.allocationPatchList(orderManagementLineV2, loginUserID, authToken);
		return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
	}

	@ApiOperation(response = OrderManagementLine.class, value = " UnAllocate Patch") // label for swagger
	@PatchMapping("/ordermanagementline/unallocate/patch")
	public ResponseEntity<?> patchUnallocateV2(@Valid @RequestBody List<OrderManagementLine> orderManagementLineV2,
											   @RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		OrderManagementLine[] updatedOrderManagementLine =
				transactionService.unallocationPatch(orderManagementLineV2, loginUserID, authToken);
		return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
	}

	@ApiOperation(response = OrderManagementLine.class, value = "Update OrderMangementLine") // label for swagger
    @PatchMapping("/ordermanagementline/{refDocNumber}")
	public ResponseEntity<?> patchOrderMangementLine(@PathVariable String refDocNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String partnerCode, @RequestParam Long lineNumber, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode,
			@Valid @RequestBody OrderManagementLine updateOrderMangementLine, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
    	OrderManagementLine createdOrderMangementLine = 
    			transactionService.updateOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, 
						partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode, 
						loginUserID, updateOrderMangementLine, authToken);
		return new ResponseEntity<>(createdOrderMangementLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OrderManagementLine.class, value = "Delete OrderManagementLine") // label for swagger
	@DeleteMapping("/ordermanagementline/{refDocNumber}")
	public ResponseEntity<?> deleteOrderManagementLine(@PathVariable String refDocNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String partnerCode, @RequestParam Long lineNumber, 
			@RequestParam String itemCode, @RequestParam String proposedStorageBin, 
			@RequestParam String proposedPackCode, @RequestParam String loginUserID,
			@RequestParam String authToken) {
    	transactionService.deleteOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
    			lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    @ApiOperation(response = OrderManagementLine.class, value = "Get a OrderManagementLine") // label for swagger 
   	@GetMapping("/ordermanagementline/updateRefFields")
   	public ResponseEntity<?> updateRefFields(@RequestParam String authToken) throws Exception{
    	transactionService.updateRef9ANDRef10(authToken);
   		return new ResponseEntity<>(HttpStatus.OK);
   	}
	
	/*
	 * -------------------------PickupHeader----------------------------------------------------
	 */
	@ApiOperation(response = PickupHeader.class, value = "Search PickupHeader") // label for swagger
	@PostMapping("/pickupheader/findPickupHeader")
	public PickupHeader[] findPickupHeader(@RequestBody SearchPickupHeader searchPickupHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPickupHeader(searchPickupHeader, authToken);
	}

	/*
	 * -------------------------PickupHeader New Stream-----------------------------------------------
	 */
	@ApiOperation(response = PickupHeader.class, value = "Search PickupHeader New") // label for swagger
	@PostMapping("/pickupheader/findPickupHeaderNew")
	public PickupHeader[] findPickupHeaderNew(@RequestBody SearchPickupHeader searchPickupHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPickupHeaderNew(searchPickupHeader, authToken);
	}
	
   @ApiOperation(response = PickupHeader.class, value = "Update PickupHeader") // label for swagger
    @PatchMapping("/pickupheader/{pickupNumber}")
	public ResponseEntity<?> patchPickupHeader(@PathVariable String pickupNumber, @RequestParam String warehouseId, 
			@RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody PickupHeader updatePickupHeader, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
		PickupHeader createdPickupHeader = 
				transactionService.updatePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
						pickupNumber, lineNumber, itemCode, loginUserID, updatePickupHeader, authToken);
		return new ResponseEntity<>(createdPickupHeader , HttpStatus.OK);
	}

	@ApiOperation(response = PickupHeader.class, value = "Update Assigned PickerId in PickupHeader") // label for swagger // label for swagger
	@PatchMapping("/pickupheader/update-assigned-picker")
	public ResponseEntity<?> patchAssignedPickerIdInPickupHeader(@Valid @RequestBody List<UpdatePickupHeader> updatePickupHeaderList,
																 @RequestParam("loginUserID") String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		PickupHeader[] updatedPickupHeader =
				transactionService.patchAssignedPickerIdInPickupHeader(loginUserID,updatePickupHeaderList, authToken);
		return new ResponseEntity<>(updatedPickupHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PickupHeader.class, value = "Delete PickupHeader") // label for swagger
	@DeleteMapping("/pickupheader/{pickupNumber}")
	public ResponseEntity<?> deletePickupHeader(@PathVariable String pickupNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam Long lineNumber, @RequestParam String itemCode, @RequestParam String proposedStorageBin, 
			@RequestParam String proposedPackCode, @RequestParam String loginUserID, @RequestParam String authToken) 
					throws IllegalAccessException, InvocationTargetException {
    	transactionService.deletePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber, 
    			lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * -------------------------PickupLine----------------------------------------------------
	 */
	@ApiOperation(response = Inventory[].class, value = "Get AdditionalBins") // label for swagger 
	@GetMapping("/pickupline/additionalBins")
	public ResponseEntity<?> getAdditionalBins(@RequestParam String warehouseId, @RequestParam String itemCode, 
			@RequestParam Long obOrdertypeId, @RequestParam String proposedPackBarCode, @RequestParam String proposedStorageBin, 
			@RequestParam String authToken) {
    	Inventory[] additionalBins = transactionService.getAdditionalBins(warehouseId, itemCode, obOrdertypeId, 
    			proposedPackBarCode, proposedStorageBin, authToken);
    	log.info("additionalBins : " + additionalBins);
		return new ResponseEntity<>(additionalBins, HttpStatus.OK);
	}
	
	@ApiOperation(response = PickupLine.class, value = "Create PickupLine") // label for swagger
	@PostMapping("/pickupline")
	public ResponseEntity<?> postPickupLine(@Valid @RequestBody List<AddPickupLine> newPickupLine, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
		PickupLine[] createdPickupLine = transactionService.createPickupLine(newPickupLine, loginUserID, authToken);
		return new ResponseEntity<>(createdPickupLine , HttpStatus.OK);
	}
	
	@ApiOperation(response = PickupLine.class, value = "Search PickupLine") // label for swagger
	@PostMapping("/pickupline/findPickupLine")
	public PickupLine[] findPickupLine(@RequestBody SearchPickupLine searchPickupLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPickupLine(searchPickupLine, authToken);
	}
	
	@ApiOperation(response = PickupLine.class, value = "Update PickupLine") // label for swagger
    @PatchMapping("/pickupline/{actualHeNo}")
	public ResponseEntity<?> patchPickupLine(@PathVariable String actualHeNo, @RequestParam String warehouseId, 
			@RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode, 
			@RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
			@Valid @RequestBody PickupLine updatePickupLine, @RequestParam String loginUserID,
			@RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
		PickupLine createdPickupLine = 
				transactionService.updatePickupLine(warehouseId, preOutboundNo, refDocNumber, 
						partnerCode, lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode, 
						actualHeNo, loginUserID, updatePickupLine, authToken);
		return new ResponseEntity<>(createdPickupLine , HttpStatus.OK);
	}
	
    @ApiOperation(response = PickupLine.class, value = "Delete PickupLine") // label for swagger
   	@DeleteMapping("/pickupline/{actualHeNo}")
   	public ResponseEntity<?> deletePickupLine(@PathVariable String actualHeNo, 
   			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
   			@RequestParam String refDocNumber, @RequestParam String partnerCode, 
   			@RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode, 
   			@RequestParam String pickedStorageBin, @RequestParam String pickedPackCode, 
   			@RequestParam String loginUserID, @RequestParam String authToken) 
   					throws IllegalAccessException, InvocationTargetException {
    	transactionService.deletePickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, 
    			pickupNumber, itemCode, actualHeNo, pickedStorageBin, pickedPackCode, loginUserID, authToken);
   		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
   	}
	
	/*
	 * ------------------------QualityHeader--------------------------------------------------------
	 */
    
    @ApiOperation(response = QualityHeader.class, value = "Create QualityHeader") // label for swagger
	@PostMapping("/qualityheader")
	public ResponseEntity<?> postQualityHeader(@Valid @RequestBody QualityHeader newQualityHeader, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
		QualityHeader createdQualityHeader = transactionService.createQualityHeader(newQualityHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdQualityHeader , HttpStatus.OK);
	}
	
	@ApiOperation(response = QualityHeader.class, value = "Search QualityHeader") // label for swagger
	@PostMapping("/qualityheader/findQualityHeader")
	public QualityHeader[] findQualityHeader(@RequestBody SearchQualityHeader searchQualityHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findQualityHeader(searchQualityHeader, authToken);
	}

	//Stream
	@ApiOperation(response = QualityHeader.class, value = "Search QualityHeader New") // label for swagger
	@PostMapping("/qualityheader/findQualityHeaderNew")
	public QualityHeader[] findQualityHeaderNew(@RequestBody SearchQualityHeader searchQualityHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findQualityHeaderNew(searchQualityHeader, authToken);
	}
	
    @ApiOperation(response = QualityHeader.class, value = "Update QualityHeader") // label for swagger
    @PatchMapping("/qualityheader/{qualityInspectionNo}")
	public ResponseEntity<?> patchQualityHeader(@PathVariable String qualityInspectionNo, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam String pickupNumber, @RequestParam String actualHeNo,
			@Valid @RequestBody QualityHeader updateQualityHeader, 
			@RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		QualityHeader updatedQualityHeader = transactionService.updateQualityHeader(warehouseId, preOutboundNo, refDocNumber, 
						partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID, updateQualityHeader, authToken);
		return new ResponseEntity<>(updatedQualityHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = QualityHeader.class, value = "Delete QualityHeader") // label for swagger
	@DeleteMapping("/qualityheader/{qualityInspectionNo}")
	public ResponseEntity<?> deleteQualityHeader(@PathVariable String qualityInspectionNo, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam String pickupNumber, @RequestParam String actualHeNo, 
			@RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
    	transactionService.deleteQualityHeader(warehouseId, preOutboundNo, refDocNumber, 
				partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * ------------------------QualityLine-----------------------------------------------------------
	 */
	@ApiOperation(response = QualityLine.class, value = "Search QualityLine") // label for swagger
	@PostMapping("/qualityline/findQualityLine")
	public QualityLine[] findQualityLine(@RequestBody SearchQualityLine searchQualityLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findQualityLine(searchQualityLine, authToken);
	}
	
	@ApiOperation(response = QualityLine.class, value = "Create QualityLine") // label for swagger
	@PostMapping("/qualityline")
	public ResponseEntity<?> postQualityLine(@Valid @RequestBody List<AddQualityLine> newQualityLine, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
		QualityLine[] createdQualityLine = transactionService.createQualityLine(newQualityLine, loginUserID, authToken);
		return new ResponseEntity<>(createdQualityLine , HttpStatus.OK);
	}
	
	 @ApiOperation(response = QualityLine.class, value = "Update QualityLine") // label for swagger
    @PatchMapping("/qualityline/{partnerCode}")
	public ResponseEntity<?> patchQualityLine(@PathVariable String partnerCode, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam Long lineNumber, 
			@RequestParam String qualityInspectionNo, @RequestParam String itemCode,
			@Valid @RequestBody QualityLine updateQualityLine, @RequestParam String loginUserID,
			@RequestParam String authToken)	throws IllegalAccessException, InvocationTargetException {
		QualityLine createdQualityLine = 
				transactionService.updateQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
						lineNumber, qualityInspectionNo, itemCode, loginUserID, updateQualityLine, authToken);
		return new ResponseEntity<>(createdQualityLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = QualityLine.class, value = "Delete QualityLine") // label for swagger
	@DeleteMapping("/qualityline/{partnerCode}")
	public ResponseEntity<?> deleteQualityLine(@PathVariable String partnerCode, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam Long lineNumber, 
			@RequestParam String qualityInspectionNo, @RequestParam String itemCode, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
    	transactionService.deleteQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
				lineNumber, qualityInspectionNo, itemCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * ---------------------------OutboundHeader--------------------------------------------------------
	 */
	@ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader") // label for swagger
	@PostMapping("/outboundheader/findOutboundHeader")
	public OutboundHeader[] findOutboundHeader(@RequestBody SearchOutboundHeader searchOutboundHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findOutboundHeader(searchOutboundHeader, authToken);
	}
	
	@ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader") // label for swagger
	@PostMapping("/outboundheader/findOutboundHeader/rfd")
	public OutboundHeader[] findOutboundHeaderForRFD(@RequestBody SearchOutboundHeader searchOutboundHeader, 
			@RequestParam String authToken) throws Exception {
		return transactionService.findOutboundHeaderForRFD (searchOutboundHeader, authToken);
	}

	@ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader New") // label for swagger
	@PostMapping("/outboundheader/findOutboundHeaderNew")
	public OutboundHeader[] findOutboundHeaderNew(@RequestBody SearchOutboundHeader searchOutboundHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findOutboundHeaderNew(searchOutboundHeader, authToken);
	}

	@ApiOperation(response = OutboundHeader.class, value = "Update OutboundHeader") // label for swagger
    @PatchMapping("/outboundheader/{preOutboundNo}")
	public ResponseEntity<?> patchOutboundHeader(@PathVariable String preOutboundNo, 
			@RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
			@Valid @RequestBody OutboundHeader updateOutboundHeader, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		OutboundHeader createdOutboundHeader = 
				transactionService.updateOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
						updateOutboundHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdOutboundHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundHeader.class, value = "Delete OutboundHeader") // label for swagger
	@DeleteMapping("/outboundheader/{preOutboundNo}")
	public ResponseEntity<?> deleteOutboundHeader(@PathVariable String preOutboundNo, 
			@RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
    	transactionService.deleteOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@ApiOperation(response = String.class, value = "Search OrderNumber") // label for swagger
	@PostMapping("/outboundheader/findOrderNumber")
	public String[] findOrderNumber(@RequestBody SearchOutboundHeader searchOutboundHeader, @RequestParam String authToken) throws Exception {
		return transactionService.findOrderNumber(searchOutboundHeader, authToken);
	}
	
	/*
	 * ----------------------OutboundLine----------------------------------------------------------
	 */
    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
	@PostMapping("/outboundline/findOutboundLine")
	public OutboundLine[] findOutboundLine(@RequestBody SearchOutboundLine searchOutboundLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findOutboundLine(searchOutboundLine, authToken);
	}
    
	@ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
	@PostMapping("/outboundline/findOutboundLine-new")
	public OutboundLine[] findOutboundLineNew(@RequestBody SearchOutboundLine searchOutboundLine,
										   @RequestParam String authToken) throws Exception {
		return transactionService.findOutboundLineNew(searchOutboundLine, authToken);
	}

	@ApiOperation(response = StockMovementReport.class, value = "Search OutboundLine for Stock movement report") // label for swagger
	@PostMapping("/outboundline/stock-movement-report/findOutboundLine")
	public StockMovementReport[] findOutboundLineForStockMovement(@RequestBody SearchOutboundLine searchOutboundLine,@RequestParam String authToken)
			throws Exception {
		return transactionService.findOutboundLineForStockMovement(searchOutboundLine,authToken);
	}

	//Stock Movement Report with Inventory Movement
	@ApiOperation(response = StockMovementReport.class, value = "Stock movement report with Inventory Movement") // label for swagger
	@PostMapping("/outboundline/stock-movement-report/v2/findOutboundLine")
	public StockMovementReport[] findStockMovement(@RequestBody SearchOutboundLine searchOutboundLine, @RequestParam String authToken) throws Exception {
		return transactionService.findStockMovementReport(searchOutboundLine, authToken);
	}

	/*
	 * --------------------------------Get all StockMovementReport---------------------------------------------------------------
	 */
	@ApiOperation(response = StockMovementReport.class, value = "Get all StockMovementReport details") // label for swagger
	@GetMapping("/outboundline/stock-movement-report")
	public ResponseEntity<?> getStockMovementReports(@RequestParam String authToken) throws Exception {
		StockMovementReport[] stockMovementReportList = transactionService.getStockMovementReports(authToken);
		return new ResponseEntity<>(stockMovementReportList, HttpStatus.OK);
	}

	/*
	 * Inventory Stock movement report renamed to Transaction History report
	 */
	@ApiOperation(response = InventoryStockReport.class, value = "Get transaction History Report")
	@PostMapping("/reports/transactionHistoryReport")
	public ResponseEntity<?> getTransactionHistoryReport(@RequestBody FindImBasicData1 findImBasicData1,
														 @RequestParam String authToken) throws java.text.ParseException {
		InventoryStockReport[] inventoryReportList = transactionService.getTransactionHistoryReport(findImBasicData1, authToken);
		return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
    @GetMapping("/outboundline/delivery/confirmation")
	public ResponseEntity<?> deliveryConfirmation (@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String loginUserID,
												   @RequestParam(required = false) Boolean webPortal, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		OutboundLine[] createdOutboundLine = 
				transactionService.deliveryConfirmation(warehouseId, preOutboundNo, refDocNumber, 
						partnerCode, loginUserID, webPortal, authToken);
		return new ResponseEntity<>(createdOutboundLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
    @PatchMapping("/outboundline/{lineNumber}")
	public ResponseEntity<?> patchOutboundLine(@PathVariable Long lineNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, 
			@RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String itemCode,
			@Valid @RequestBody OutboundLine updateOutboundLine, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		OutboundLine updatedOutboundLine = 
				transactionService.updateOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, 
						lineNumber, itemCode, loginUserID, updateOutboundLine, authToken);
		return new ResponseEntity<>(updatedOutboundLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLines") // label for swagger
    @PatchMapping("/outboundline/lineNumbers")
	public ResponseEntity<?> patchOutboundLines(@Valid @RequestBody List<UpdateOutboundLine> updateOutboundLine, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
			throws IllegalAccessException, InvocationTargetException {
    	com.tekclover.wms.core.model.transaction.OutboundLine[] outboundLines = 
    			transactionService.updateOutboundLines(loginUserID, updateOutboundLine, authToken);
		return new ResponseEntity<>(outboundLines, HttpStatus.OK);
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Delete OutboundLine") // label for swagger
	@DeleteMapping("/outboundline/{lineNumber}")
	public ResponseEntity<?> deleteOutboundLine(@PathVariable Long lineNumber, 
			@RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber, 
			@RequestParam String partnerCode, @RequestParam String itemCode, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
    	transactionService.deleteOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, 
    			itemCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    @ApiOperation(response = OutboundLine.class, value = "Update Required Delivery Date") // label for swagger 
    @PatchMapping("/outboundline/requiredDeliveryDate")
   	public ResponseEntity<?> doRequiredDeliveryDateUpdate (@RequestBody UpdateRequestDeliveryDate updateRequestDeliveryDate,
   			@RequestParam String authToken) 
   					throws IllegalAccessException, InvocationTargetException {
    	transactionService.doRequiredDeliveryDateUpdate(updateRequestDeliveryDate, authToken);
   		return new ResponseEntity<>(HttpStatus.OK);
   	}
    
    /*
     * --------------------------------OutboundReversal-----------------------------------------------
     */
    @ApiOperation(response = OutboundReversal.class, value = "Search OutboundReversal") // label for swagger
	@PostMapping("/outboundreversal/findOutboundReversal")
	public OutboundReversal[] findOutboundReversal(@RequestBody SearchOutboundReversal searchOutboundReversal,
			@RequestParam String authToken) throws Exception {
		return transactionService.findOutboundReversal(searchOutboundReversal, authToken);
	}

	//Stream
	@ApiOperation(response = OutboundReversal.class, value = "Search OutboundReversal New") // label for swagger
	@PostMapping("/outboundreversal/findOutboundReversalNew")
	public OutboundReversal[] findOutboundReversalNew(@RequestBody SearchOutboundReversal searchOutboundReversal,
			@RequestParam String authToken) throws Exception {
		return transactionService.findOutboundReversalNew(searchOutboundReversal, authToken);
	}
    
    /*--------------------Shipping Reversal-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLine.class, value = "Get Delivery Lines") // label for swagger 
   	@GetMapping("/outboundreversal/reversal/new")
   	public ResponseEntity<?> doReversal(@RequestParam String refDocNumber, @RequestParam String itemCode,
   			@RequestParam String loginUserID, @RequestParam String authToken) 
   					throws IllegalAccessException, InvocationTargetException {
       	OutboundReversal[] deliveryLines = transactionService.doReversal(refDocNumber, itemCode, loginUserID, authToken);
       	log.info("deliveryLines : " + deliveryLines);
   		return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
   	}
    
    /*
     * ----------------------------Reports-----------------------------------------------------------
     */
    @ApiOperation(response = StockReport.class, value = "Get StockReport") // label for swagger 
   	@GetMapping("/reports/stockReport")
   	public ResponseEntity<?> getStockReport(@RequestParam List<String> warehouseId, 
   			@RequestParam(required = false) List<String> itemCode, 
   			@RequestParam(required = false) String itemText, 
   			@RequestParam String stockTypeText,
   			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "itemCode") String sortBy,
   			@RequestParam String authToken) {
    	PaginatedResponse<StockReport> stockReport = 
    			transactionService.getStockReports(warehouseId, itemCode, itemText, stockTypeText, 
    					pageNo, pageSize, sortBy, authToken);
   		return new ResponseEntity<>(stockReport, HttpStatus.OK);
   	}

	@ApiOperation(response = StockReport.class, value = "Get All Stock Report") // label for swagger
	@GetMapping("/reports/stockReport-all")
	public ResponseEntity<?> getAllStockReport(@RequestParam List<String> warehouseId,
											   @RequestParam(required = false) List<String> itemCode,
											   @RequestParam(required = false) String itemText,
											   @RequestParam(required = true) String stockTypeText,@RequestParam String authToken) {
		StockReport[] stockReportList = transactionService.getAllStockReports(warehouseId, itemCode, itemText, stockTypeText,authToken);
		return new ResponseEntity<>(stockReportList, HttpStatus.OK);
	}
    
    /*
	 * Inventory Report
	 */
    @ApiOperation(response = InventoryReport.class, value = "Get Inventory Report") // label for swagger 
	@GetMapping("/reports/inventoryReport")
	public ResponseEntity<?> getInventoryReport(@RequestParam List<String> warehouseId, 
			@RequestParam(required = false) List<String> itemCode, 
			@RequestParam(required = false) String storageBin, 
			@RequestParam(required = false) String stockTypeText, 
			@RequestParam(required = false) List<String> stSectionIds, 
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "itemCode") String sortBy,
			@RequestParam String authToken) {
    	PaginatedResponse<InventoryReport> inventoryReportList = 	
    			transactionService.getInventoryReport(warehouseId, itemCode, storageBin, stockTypeText, stSectionIds, 
    					pageNo, pageSize, sortBy, authToken);
		return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
	}
    
    /*
   	 * Stock movement report
   	 */
    @ApiOperation(response = StockMovementReport.class, value = "Get StockMovement Report") // label for swagger 
   	@GetMapping("/reports/stockMovementReport")
   	public ResponseEntity<?> getStockMovementReport(@RequestParam String warehouseId, 
   			@RequestParam String itemCode, @RequestParam String fromCreatedOn, 
   			@RequestParam String toCreatedOn, @RequestParam String authToken) throws Exception {
       	StockMovementReport[] inventoryReportList = 
       			transactionService.getStockMovementReport(warehouseId, itemCode, fromCreatedOn, toCreatedOn, authToken);
   		return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
   	}
    
    /*
   	 * Order status report
   	 */
    @ApiOperation(response = OrderStatusReport.class, value = "Get OrderStatus Report") // label for swagger 
   	@PostMapping("/reports/orderStatusReport")
   	public ResponseEntity<?> getOrderStatusReport(@RequestBody @Valid SearchOrderStatusReport request,
   			@RequestParam String authToken) throws ParseException, Exception {
       	OrderStatusReport[] orderStatusReportList = transactionService.getOrderStatusReport(request, authToken);
   		return new ResponseEntity<>(orderStatusReportList, HttpStatus.OK);
   	}
    
    /*
   	 * Shipment Delivery 
   	 */
    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report") // label for swagger 
   	@GetMapping("/reports/shipmentDelivery")
   	public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String warehouseId, 
   			@RequestParam(required = false) String fromDeliveryDate,
   			@RequestParam(required = false) String toDeliveryDate, 
   			@RequestParam(required = false) String storeCode, 
   			@RequestParam(required = false) List<String> soType, 
   			@RequestParam String orderNumber, 
   			@RequestParam String authToken) 
   					throws ParseException, Exception {
    	ShipmentDeliveryReport[] shipmentDeliveryList = transactionService.getShipmentDeliveryReport(warehouseId, 
       			fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, authToken);
   		return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
   	}
    
    @ApiOperation(response = ShipmentDeliverySummaryReport.class, value = "Get ShipmentDeliverySummary Report") // label for swagger 
   	@GetMapping("/reports/shipmentDeliverySummary1")
   	public ResponseEntity<?> getShipmentDeliveryReport1(@RequestParam String fromDeliveryDate, 
   			@RequestParam String toDeliveryDate, @RequestParam(required = false) List<String> customerCode, 
   			@RequestParam String authToken,@RequestParam(required = true) String warehouseId) throws ParseException, Exception {
    	ShipmentDeliverySummaryReport shipmentDeliverySummaryReport = 
    			transactionService.getShipmentDeliverySummaryReport(fromDeliveryDate, toDeliveryDate, customerCode,warehouseId, authToken);
    	return new ResponseEntity<>(shipmentDeliverySummaryReport, HttpStatus.OK);
   	}
    
    /*
   	 * Inventory Stock movement report
   	 */
    @ApiOperation(response = Optional.class, value = "Get Opening Stock Report")  
   	@PostMapping("/reports/openingStockReport")
   	public ResponseEntity<?> getInventoryStockReport(@RequestBody FindImBasicData1 findImBasicData1,
   			@RequestParam String authToken) throws java.text.ParseException {
		InventoryStockReport[] inventoryReportList = transactionService.getInventoryStockReport(findImBasicData1, authToken);
   		return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
   	}
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /*
   	 * Shipment Dispatch Summary
   	 */
    @ApiOperation(response = ShipmentDispatchSummaryReport.class, value = "Get ShipmentDispatchSummary Report") // label for swagger 
   	@GetMapping("/shipmentDispatchSummary")
   	public ResponseEntity<?> getShipmentDispatchSummaryReport(@RequestParam String fromDeliveryDate, 
   			@RequestParam String toDeliveryDate, @RequestParam(required = false) List<String> customerCode, 
   			@RequestParam String authToken,@RequestParam(required = true) String warehouseId) throws ParseException, Exception {
    	ShipmentDispatchSummaryReport shipmentDispatchSummary = 
    			transactionService.getShipmentDispatchSummaryReport(fromDeliveryDate, toDeliveryDate, customerCode,warehouseId, authToken);
    	return new ResponseEntity<>(shipmentDispatchSummary, HttpStatus.OK);
   	}
    
    /*
   	 * Receipt Confirmation
   	 */
    @ApiOperation(response = ReceiptConfimationReport.class, value = "Get ReceiptConfimation Report") // label for swagger 
   	@GetMapping("/reports/receiptConfirmation")
   	public ResponseEntity<?> getReceiptConfimationReport(@RequestParam String asnNumber, 
   			@RequestParam String authToken) throws Exception {
    	ReceiptConfimationReport receiptConfimationReport = transactionService.getReceiptConfimationReport(asnNumber, authToken);
    	return new ResponseEntity<>(receiptConfimationReport, HttpStatus.OK);
   	}
    
    /*
     * MobileDashboard
     */
    @ApiOperation(response = MobileDashboard.class, value = "Get MobileDashboard Report") // label for swagger 
   	@GetMapping("/reports/dashboard/mobile")
   	public ResponseEntity<?> getMobileDashboard(@RequestParam String warehouseId,
   			@RequestParam String authToken) throws Exception {
       	MobileDashboard dashboard = transactionService.getMobileDashboard(warehouseId, authToken);
   		return new ResponseEntity<>(dashboard, HttpStatus.OK);
   	}
    
    /*
     * -----------------------------Perpetual Count----------------------------------------------------
     */
    @ApiOperation(response = PerpetualHeader.class, value = "Get all PerpetualHeader details") // label for swagger
	@GetMapping("/perpetualheader")
	public ResponseEntity<?> getPerpetualHeaders(@RequestParam String authToken) throws Exception{
		PerpetualHeader[] perpetualheaderList = transactionService.getPerpetualHeaders(authToken);
		return new ResponseEntity<>(perpetualheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PerpetualHeader.class, value = "Get a PerpetualHeader") // label for swagger 
	@GetMapping("/perpetualheader/{cycleCountNo}")
	public ResponseEntity<?> getPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
			@RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
			@RequestParam String authToken) throws Exception{
    	PerpetualHeader perpetualheader =
    			transactionService.getPerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, 
    					movementTypeId, subMovementTypeId, authToken);
    	log.info("PerpetualHeader : " + perpetualheader);
		return new ResponseEntity<>(perpetualheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = PerpetualHeaderEntity[].class, value = "Search PerpetualHeader") // label for swagger
	@PostMapping("/perpetualheader/findPerpetualHeader")
	public PerpetualHeaderEntity[] findPerpetualHeader(@RequestBody SearchPerpetualHeader searchPerpetualHeader,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPerpetualHeader(searchPerpetualHeader, authToken);
	}
    
    @ApiOperation(response = PerpetualHeader.class, value = "Create PerpetualHeader") // label for swagger
	@PostMapping("/perpetualheader")
	public ResponseEntity<?> postPerpetualHeader(@Valid @RequestBody AddPerpetualHeader newPerpetualHeader, 
			@RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		PerpetualHeader createdPerpetualHeader = 
				transactionService.createPerpetualHeader(newPerpetualHeader, loginUserID, authToken);
		return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
	}
    
    /*
     * Pass From and To dates entered in Header screen into INVENOTRYMOVEMENT tables in IM_CTD_BY field 
     * along with selected MVT_TYP_ID/SUB_MVT_TYP_ID values and fetch the below values
     */
    @ApiOperation(response = PerpetualLineEntity[].class, value = "Create PerpetualHeader") // label for swagger
   	@PostMapping("/perpetualheader/run")
   	public ResponseEntity<?> postRunPerpetualHeader(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
   			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
    	PerpetualLineEntity[] perpetualLineEntity = 
   				transactionService.runPerpetualHeader(runPerpetualHeader, authToken);
   		return new ResponseEntity<>(perpetualLineEntity , HttpStatus.OK);
   	}

	@ApiOperation(response = PerpetualLineEntity[].class, value = "Create PerpetualHeader Stream") // label for swagger
   	@PostMapping("/perpetualheader/runNew")
   	public ResponseEntity<?> postRunPerpetualHeaderStream(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
   			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
    	PerpetualLineEntity[] perpetualLineEntity =
   				transactionService.runPerpetualHeaderNew(runPerpetualHeader, authToken);
   		return new ResponseEntity<>(perpetualLineEntity , HttpStatus.OK);
   	}

    @ApiOperation(response = PerpetualHeader.class, value = "Update PerpetualHeader") // label for swagger
    @PatchMapping("/perpetualheader/{cycleCountNo}")
	public ResponseEntity<?> patchPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId, 
			@RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
			@Valid @RequestBody UpdatePerpetualHeader updatePerpetualHeader, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		PerpetualHeader createdPerpetualHeader = 
				transactionService.updatePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
						subMovementTypeId, loginUserID, updatePerpetualHeader, authToken);
		return new ResponseEntity<>(createdPerpetualHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PerpetualHeader.class, value = "Delete PerpetualHeader") // label for swagger
	@DeleteMapping("/perpetualheader/{cycleCountNo}")
	public ResponseEntity<?> deletePerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId, 
			@RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
    	transactionService.deletePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId, 
    			subMovementTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    //-------------------------------PerpetualLine---------------------------------------------------------------------
    
    //-------------------------------PerpetualLine---------------------------------------------------------------------
    @ApiOperation(response = PerpetualHeaderEntity.class, value = "SearchPerpetualLine") // label for swagger
	@PostMapping("/findPerpetualLine")
	public PerpetualLine[] findPerpetualHeader(@RequestBody SearchPerpetualLine searchPerpetualLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPerpetualLine(searchPerpetualLine, authToken);
	}

	@ApiOperation(response = PerpetualHeaderEntity.class, value = "SearchPerpetualLine Stream") // label for swagger
	@PostMapping("/findPerpetualLineNew")
	public PerpetualLine[] findPerpetualHeaderStream(@RequestBody SearchPerpetualLine searchPerpetualLine,
			@RequestParam String authToken) throws Exception {
		return transactionService.findPerpetualLineStream(searchPerpetualLine, authToken);
	}
    
    @ApiOperation(response = PerpetualLine[].class, value = "Update AssignHHTUser") // label for swagger
    @PatchMapping("/perpetualline/assigingHHTUser")
	public ResponseEntity<?> patchAssingHHTUser (@RequestBody List<AssignHHTUserCC> assignHHTUser, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
					throws IllegalAccessException, InvocationTargetException {
		PerpetualLine[] createdPerpetualLine = transactionService.updateAssingHHTUser (assignHHTUser, loginUserID, authToken);
		return new ResponseEntity<>(createdPerpetualLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = PerpetualLine.class, value = "Update PerpetualLine") // label for swagger
    @PatchMapping("/perpetualline/{cycleCountNo}")
	public ResponseEntity<?> patchAssingHHTUser (@PathVariable String cycleCountNo, 
			@RequestBody List<UpdatePerpetualLine> updatePerpetualLine, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		PerpetualUpdateResponse createdPerpetualLine = 
				transactionService.updatePerpetualLine (cycleCountNo, updatePerpetualLine, loginUserID, authToken);
		return new ResponseEntity<>(createdPerpetualLine , HttpStatus.OK);
	}
    
    /*
     * -----------------------------Periodic Count----------------------------------------------------
     */
    @ApiOperation(response = PeriodicHeader.class, value = "Get all PeriodicHeader details") // label for swagger
	@GetMapping("/periodicheader")
	public ResponseEntity<?> getPeriodicHeaders(@RequestParam String authToken) throws Exception{
		PeriodicHeaderEntity[] PeriodicheaderList = transactionService.getPeriodicHeaders(authToken);
		return new ResponseEntity<>(PeriodicheaderList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Get a PeriodicHeader") // label for swagger 
	@GetMapping("/periodicheader/{cycleCountNo}")
	public ResponseEntity<?> getPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
			@RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
			@RequestParam String authToken) throws Exception{
    	PeriodicHeader[] Periodicheader = 
    			transactionService.getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo, 
    					movementTypeId, subMovementTypeId, authToken);
		return new ResponseEntity<>(Periodicheader, HttpStatus.OK);
	}
    
	@ApiOperation(response = PeriodicHeader[].class, value = "Search PeriodicHeader") // label for swagger
	@PostMapping("/periodicheader/findPeriodicHeader")
	public ResponseEntity<?> findPeriodicHeader(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
			@RequestParam String authToken) throws Exception {
		PeriodicHeaderEntity[] results = transactionService.findPeriodicHeader(searchPeriodicHeader, authToken);
		return new ResponseEntity<>(results, HttpStatus.OK);
	}
	//Stream
	@ApiOperation(response = PeriodicHeader[].class, value = "Search PeriodicHeader Stream") // label for swagger
	@PostMapping("/periodicheader/findPeriodicHeaderNew")
	public ResponseEntity<?> findPeriodicHeaderNew(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
			@RequestParam String authToken) throws Exception {
		PeriodicHeader[] results = transactionService.findPeriodicHeaderStream(searchPeriodicHeader, authToken);
		return new ResponseEntity<>(results, HttpStatus.OK);
	}

    @ApiOperation(response = PeriodicHeader.class, value = "Create PeriodicHeader") // label for swagger
	@PostMapping("/periodicheader")
	public ResponseEntity<?> postPeriodicHeader(@Valid @RequestBody AddPeriodicHeader newPeriodicHeader, 
			@RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		PeriodicHeaderEntity createdPeriodicHeader = transactionService.createPeriodicHeader(newPeriodicHeader, loginUserID, authToken);
		log.info("createdPeriodicHeader:" + createdPeriodicHeader);

		/* Call Batch */
//		transactionService.createCSV(newPeriodicHeader.getPeriodicLine());
//		batchJobScheduler.runJobPeriodic();
		return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
	}
    
    @ApiOperation(response = PeriodicLineEntity.class, value = "Search Inventory") // label for swagger
	@PostMapping("/periodicheader/run/pagination")
	public ResponseEntity<?> findInventory(@RequestParam String warehouseId, 
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "100") Integer pageSize,
			@RequestParam(defaultValue = "itemCode") String sortBy,
			@RequestParam String authToken) 
			throws Exception {
		Page<?> periodicLineEntity = transactionService.runPeriodicHeader(warehouseId, pageNo, pageSize, sortBy, authToken);
		return new ResponseEntity<>(periodicLineEntity , HttpStatus.OK);
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Update PeriodicHeader") // label for swagger
    @PatchMapping("/periodicheader/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId, 
			@RequestParam Long cycleCountTypeId, @RequestBody UpdatePeriodicHeader updatePeriodicHeader, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
					throws IllegalAccessException, InvocationTargetException {
		PeriodicHeader createdPeriodicHeader = 
				transactionService.updatePeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo, 
						loginUserID, updatePeriodicHeader, authToken);
		return new ResponseEntity<>(createdPeriodicHeader , HttpStatus.OK);
	}
    
    @ApiOperation(response = PeriodicHeader.class, value = "Delete PeriodicHeader") // label for swagger
	@DeleteMapping("/periodicheader/{cycleCountNo}")
	public ResponseEntity<?> deletePeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId, 
			@RequestParam Long cycleCountTypeId, @RequestParam String loginUserID, @RequestParam String authToken) {
    	transactionService.deletePeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
    
    //-------------------------------PeriodicLine---------------------------------------------------------------------
    @ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine") // label for swagger
	@PostMapping("/periodicline/findPeriodicLine")
	public com.tekclover.wms.core.model.transaction.PeriodicLine[] findPeriodicLine (@RequestBody SearchPeriodicLine searchPeriodicLine, 
			@RequestParam String authToken) throws Exception {
		return transactionService.findPeriodicLine (searchPeriodicLine, authToken);
	}

	//Stream
	@ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine Stream") // label for swagger
	@PostMapping("/periodicline/findPeriodicLineNew")
	public com.tekclover.wms.core.model.transaction.PeriodicLine[] findPeriodicLineNew (@RequestBody SearchPeriodicLine searchPeriodicLine,
																						@RequestParam String authToken) throws Exception {
		return transactionService.findPeriodicLineNew (searchPeriodicLine, authToken);
	}
    
    @ApiOperation(response = PeriodicLine[].class, value = "Update AssignHHTUser") // label for swagger
    @PatchMapping("/periodicline/assigingHHTUser")
	public ResponseEntity<?> assigingHHTUser (@RequestBody List<AssignHHTUserCC> assignHHTUser, 
			@RequestParam String loginUserID, @RequestParam String authToken) 
					throws IllegalAccessException, InvocationTargetException {
		PeriodicLine[] createdPeriodicLine = 
				transactionService.updatePeriodicLineAssingHHTUser (assignHHTUser, loginUserID, authToken);
		return new ResponseEntity<>(createdPeriodicLine , HttpStatus.OK);
	}
    
    @ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine") // label for swagger
    @PatchMapping("/periodicline/{cycleCountNo}")
	public ResponseEntity<?> patchPeriodicLine (@PathVariable String cycleCountNo, 
			@RequestBody List<UpdatePeriodicLine> updatePeriodicLine, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		PeriodicUpdateResponse updatedPeriodicLine = 
				transactionService.updatePeriodicLine (cycleCountNo, updatePeriodicLine, loginUserID, authToken);
		return new ResponseEntity<>(updatedPeriodicLine , HttpStatus.OK);
	}
    
    //--------------------------Schedule-Report------------------------------------------------------
    @ApiOperation(response = InventoryReport.class, value = "Report Inventory") // label for swagger
    @GetMapping("/reports/inventoryReport/schedule")
	public ResponseEntity<?> scheduleInventoryReport (@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
    	transactionService.getInventoryReport (authToken);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(response = Dashboard.class, value = "Get Dashboard Counts") // label for swagger
	@GetMapping("/reports/dashboard/get-count")
	public ResponseEntity<?> getDashboardCount(@RequestParam String warehouseId, @RequestParam String authToken) throws Exception {
		Dashboard dashboard = transactionService.getDashboardCount(warehouseId,authToken);
		return new ResponseEntity<>(dashboard, HttpStatus.OK);
	}

	@ApiOperation(response = Dashboard.class, value = "Get Dashboard Fast Slow moving Dashboard") // label for swagger
	@PostMapping("/reports/dashboard/get-fast-slow-moving")
	public ResponseEntity<?> getFastSlowMovingDashboard(@RequestBody FastSlowMovingDashboardRequest fastSlowMovingDashboardRequest,
														@RequestParam String authToken) throws Exception {
		FastSlowMovingDashboard[] dashboard = transactionService.getFastSlowMovingDashboard(fastSlowMovingDashboardRequest,authToken);
		return new ResponseEntity<>(dashboard, HttpStatus.OK);
	}
	
	//----------------------Orders------------------------------------------------------------------
	@ApiOperation(response = ShipmentOrder.class, value = "Create Shipment Order") // label for swagger
	@PostMapping("/warehouse/outbound/so")
	public ResponseEntity<?> postShipmenOrder(@Valid @RequestBody ShipmentOrder shipmenOrder, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		WarehouseApiResponse createdSO = transactionService.postSO(shipmenOrder, authToken);
		return new ResponseEntity<>(createdSO, HttpStatus.OK);
	}
	
	// File Upload - Orders
	@ApiOperation(response = ShipmentOrder.class, value = "Create Shipment Order") // label for swagger
    @PostMapping("/warehouse/outbound/so/upload")
    public ResponseEntity<?> postShipmenOrderUpload (@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response = fileStorageService.processSOOrders(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@ApiOperation(response = ASN.class, value = "Create ASN Order") // label for swagger
	@PostMapping("/warehouse/inbound/asn")
	public ResponseEntity<?> postASN(@Valid @RequestBody ASN asn, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		WarehouseApiResponse createdSO = transactionService.postASN(asn, authToken);
		return new ResponseEntity<>(createdSO, HttpStatus.OK);
	}

	//-----------------------------------------ORDER APIs---------------------------------------------------------
	@ApiOperation(response = InboundOrder.class, value = "Get all InboundOrder Sucess Orders") // label for swagger
	@GetMapping("/orders/inbound/success")
	public ResponseEntity<?> getAll(@RequestParam String authToken) {
		InboundOrder[] inboundOrderList = transactionService.getInboundOrders(authToken);
		return new ResponseEntity<>(inboundOrderList, HttpStatus.OK);
	}
	
	@ApiOperation(response = InboundOrder.class, value = "Get inbound Order by id ") // label for swagger
	@GetMapping("/orders/inbound/{orderId}")
	public ResponseEntity<?> getInboundOrdersById(@PathVariable String orderId,@RequestParam String authToken) {
		InboundOrder orders = transactionService.getInboundOrderById(orderId,authToken);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
	
	@ApiOperation(response = InboundIntegrationLog.class, value = "Get Failed Orders") // label for swagger 
   	@GetMapping("/orders/inbound/failed")
   	public ResponseEntity<?> getFailedInbounOrders(@RequestParam String authToken) throws Exception {
       	InboundIntegrationLog[] orders = transactionService.getFailedInboundOrders(authToken);
   		return new ResponseEntity<>(orders, HttpStatus.OK);
   	}
	
	//-----------------------------------------------------------------------------------------------------------

	@ApiOperation(response = OutboundOrder.class, value = "Get all Outbound Success Orders") // label for swagger
   	@GetMapping("/orders/outbound/success")
   	public ResponseEntity<?> getOBAllOrders(@RequestParam String authToken) {
   		OutboundOrder[] outboundOrderList = transactionService.getOBOrders(authToken);
   		return new ResponseEntity<>(outboundOrderList, HttpStatus.OK);
   	}
	
	@ApiOperation(response = OutboundOrder.class, value = "Get a Orders") // label for swagger 
   	@GetMapping("/orders/outbound/byDate")
   	public ResponseEntity<?> getOBOrdersByDate(@RequestParam String orderStartDate, 
   			@RequestParam String orderEndDate, @RequestParam String authToken) throws Exception {
       	OutboundOrder[] orders = transactionService.getOBOrderByDate(orderStartDate, orderEndDate, authToken);
   		return new ResponseEntity<>(orders, HttpStatus.OK);
   	}
	
	@ApiOperation(response = OutboundOrder.class, value = "Get outbound Order by id ") // label for swagger
	@GetMapping("/orders/outbound/{orderId}")
	public ResponseEntity<?> getOutboundOrdersById(@PathVariable String orderId,@RequestParam String authToken) {
		OutboundOrder orders = transactionService.getOutboundOrdersById(orderId,authToken);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@ApiOperation(response = OutboundIntegrationLog.class, value = "Get Failed OutboundOrders") // label for swagger 
   	@GetMapping("/orders/outbound/failed")
   	public ResponseEntity<?> getFailedOutboundOrders(@RequestParam String authToken) throws Exception {
       	OutboundIntegrationLog[] orders = transactionService.getFailedOutboundOrders(authToken);
   		return new ResponseEntity<>(orders, HttpStatus.OK);
   	}

	//============================================STREAMING==========================================================

	@GetMapping(value = "/streaming/inventoryMovement")
	public ResponseEntity<StreamingResponseBody> findInventoryMovement() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findInventoryMovementByStreaming();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamOutboundHeader")
	public ResponseEntity<StreamingResponseBody> findStreamOutboundHeader() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamOutboundHeader();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@PostMapping(value = "/streaming/findStreamGrHeader")
	public ResponseEntity<?> findStreamgrheader() throws ExecutionException, InterruptedException {
//		StreamingResponseBody responseBody = transactionService.findStreamGrHeader();
		List<GrHeaderStream> responseBody = transactionService.streamGrHeader();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamStagingHeader")
	public ResponseEntity<StreamingResponseBody> findStreamStagingHeader() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamStagingHeader();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamPutAwayHeader")
	public ResponseEntity<StreamingResponseBody> findStreamPutAwayheader() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamPutAwayHeader();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamInboundHeader")
	public ResponseEntity<StreamingResponseBody> findStreamInboundheader() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamInboundHeader();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamPreInboundHeader")
	public ResponseEntity<StreamingResponseBody> findStreamPreInboundheader() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamPreInboundHeader();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamPreOutboundHeader")
	public ResponseEntity<StreamingResponseBody> findStreamPreOutboundheader() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamPreOutboundHeader();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamOrderManagementLine")
	public ResponseEntity<StreamingResponseBody> findStreamOrderManagementLine() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamOrderManagementLine();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamPickupHeader")
	public ResponseEntity<StreamingResponseBody> findStreamPickupHeader() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamPickupHeader();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamQualityHeader")
	public ResponseEntity<StreamingResponseBody> findStreamQualityHeader() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamQualityHeader();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamImBasicData1")
	public ResponseEntity<StreamingResponseBody> findStreamImBasicData1() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamImBasicData1();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	//Filter
	@PostMapping(value = "/streaming/findStreamImBasicData1New")
	public ResponseEntity<?> findStreamImBasicData1New(@RequestBody SearchImBasicData1 searchImBasicData1) throws ExecutionException, InterruptedException {
		List<ImBasicData1> responseBody = transactionService.getAllImBasicData1(searchImBasicData1);
		return new ResponseEntity<>(responseBody, HttpStatus.OK);
//		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	@GetMapping(value = "/streaming/findStreamStorageBin")
	public ResponseEntity<?> findStreamStorageBin() throws ExecutionException, InterruptedException {
		StreamingResponseBody responseBody = transactionService.findStreamStorageBin();
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
	}

	//PickerDenialReport
	@ApiOperation(response = PickerDenialReport.class, value = "Search PickerDenialReport") // label for swagger
	@PostMapping("/pickupline/findPickerDenialReport")
	public PickerDenialReport findPickerDenialReport(@RequestBody SearchPickupLine searchPickupLine, @RequestParam String authToken) throws Exception {
		return transactionService.pickerDenialReport(searchPickupLine, authToken);
	}

	//cancelQualityLine
	@ApiOperation(response = QualityLine.class, value = "Cancel QualityLine") // label for swagger
	@PostMapping("/qualityline/cancelQualityLine")
	public ResponseEntity<?> cancelQualityLine(@Valid @RequestBody List<ReversalInput> reversalInputList, @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
		WarehouseApiResponse response = transactionService.batchQualityReversalV2(reversalInputList, loginUserID, authToken);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	//OrderManagementLineGroupByItem
	@ApiOperation(response = OrderManagementLineGroupByItem.class, value = "OrderManagement Line Order Group by Item") // label for swagger
	@GetMapping("//ordermanagementline/getItemList")
	public ResponseEntity<?> getItemCodeList(@RequestParam String warehouseId, @RequestParam String authToken) {
		OrderManagementLineGroupByItem[] orders = transactionService.getItemCodeList(warehouseId, authToken);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	//=========================================Update=====================================

	@ApiOperation(response = InboundOrder.class, value = "Update InboundOrder details")
	@PatchMapping("/orders/update/inboundOrder")
	public ResponseEntity<?> updateInboundOrder(@RequestParam String orderId, @RequestBody UpdateInboundOrder updateInboundOrder, @RequestParam String authToken) throws Exception {
		InboundOrder inboundOrder = transactionService.updateInboundOrder(orderId, updateInboundOrder, authToken);
		return new ResponseEntity<>(inboundOrder, HttpStatus.OK);
	}


	@ApiOperation(response = OutboundOrder.class, value = "Update OutboundOrder details")
	@PatchMapping("/orders/update/outboundOrder")
	public ResponseEntity<?> updateOutboundOrder(@RequestParam String orderId, @RequestBody UpdateOutboundOrder updateOutboundOrder, @RequestParam String authToken) throws Exception {
		OutboundOrder outboundOrder = transactionService.updateOutboundOrder(orderId, updateOutboundOrder, authToken);
		return new ResponseEntity<>(outboundOrder, HttpStatus.OK);
	}

	//==================================================Delete=========================================================

	@ApiOperation(response = InboundOrder.class, value = " Delete InboundOrder details")
	@DeleteMapping("/orders/delete/inboundOrder")
	public ResponseEntity<?> deleteInboundOrder(@RequestParam String orderId, @RequestParam String authToken) throws Exception {
		transactionService.deleteInboundOrder(orderId, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@ApiOperation(response = OutboundOrder.class, value = " Delete OutboundOrder details")
	@DeleteMapping("/orders/delete/outboundOrder")
	public ResponseEntity<?> deleteOutboundOrder(@RequestParam String orderId, @RequestParam String authToken) throws Exception {
		transactionService.deleteOutboundOrder(orderId, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}