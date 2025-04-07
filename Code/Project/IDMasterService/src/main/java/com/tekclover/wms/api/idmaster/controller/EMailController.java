package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.email.*;
import com.tekclover.wms.api.idmaster.model.outboundheader.PreOutboundHeader;
import com.tekclover.wms.api.idmaster.model.outboundheader.SearchPreOutboundHeader;
import com.tekclover.wms.api.idmaster.model.pickerdenial.PickerDenialReport;
import com.tekclover.wms.api.idmaster.model.pickerdenial.SearchPickupLine;
import com.tekclover.wms.api.idmaster.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Api(tags = {"EMail"}, value = "EMail  Operations related to EMailController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "EMail",description = "Operations related to EMail ")})
@RequestMapping("/email")
@RestController
public class EMailController {
	
	@Autowired
	EMailDetailsService eMailDetailsService;
	@Autowired
	SendMailService sendMailService;

	@Autowired
	ReportService reportService;

	@Autowired
	FileStorageService fileStorageService;

	@Autowired
	TransactionService transactionService;

	@ApiOperation(response = EMailDetails.class, value = "Add Email") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postEmail(@Valid @RequestBody AddEMailDetails newEmail)
			throws IllegalAccessException, InvocationTargetException, IOException {
		EMailDetails addMail = eMailDetailsService.createEMailDetails(newEmail);
		return new ResponseEntity<>(addMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Update Email") // label for swagger
	@PatchMapping("/{id}")
	public ResponseEntity<?> patchEmail(@PathVariable Long id, @Valid @RequestBody AddEMailDetails updateEmail)
			throws IllegalAccessException, InvocationTargetException, IOException {
		EMailDetails updateMail = eMailDetailsService.updateEMailDetails(id,updateEmail);
		return new ResponseEntity<>(updateMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Get Email") // label for swagger
	@GetMapping("/{id}")
	public ResponseEntity<?> getEmail(@PathVariable Long id)
			throws IllegalAccessException, InvocationTargetException, IOException {
		EMailDetails getMail = eMailDetailsService.getEMailDetails(id);
		return new ResponseEntity<>(getMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Get all Email") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAllEmail()
			throws IllegalAccessException, InvocationTargetException, IOException {
		List<EMailDetails> getAllMail = eMailDetailsService.getEMailDetailsList();
		return new ResponseEntity<>(getAllMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Delete Email") // label for swagger
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteEmail(@PathVariable Long id)
			throws IllegalAccessException, InvocationTargetException, IOException {
		eMailDetailsService.deleteEMailDetails(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//send Mail
	@ApiOperation(response = EMailDetails.class, value = "Send Email") // label for swagger
	@GetMapping("/sendMail")
	public ResponseEntity<?> sendEmail()
			throws IOException, MessagingException {
		sendMailService.sendMail();
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Un Delete Email") // label for swagger
	@GetMapping("/undelete/{id}")
	public ResponseEntity<?> unDeleteEmail(@PathVariable Long id)
			throws IllegalAccessException, InvocationTargetException, IOException {
		EMailDetails getMail = eMailDetailsService.undeleteEMailDetails(id);
		return new ResponseEntity<>(getMail,HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Failed Order Send Email") // label for swagger
	@PostMapping("/failedOrder/sendMail")
	public ResponseEntity<?> failedOrderSendEmail(@RequestBody OrderFailedInput orderFailedInput) throws Exception {
		sendMailService.sendMail(orderFailedInput);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Scheduled(cron = "0 0 1 * * ?")
	@ApiOperation(response = Optional.class, value = "Email Sending")
	@PostMapping("/send-report")
	public ResponseEntity<?> sendReport() throws Exception {

		SearchPreOutboundHeader searchPreOutboundHeader = new SearchPreOutboundHeader();

		// Set the startOrderDate to 12:00 AM and endOrderDate to 11:59 PM of the current day
		Calendar calendar = Calendar.getInstance();

		// Move to the previous day
		calendar.add(Calendar.DATE, -1);

		Date startOrderDate;
		// Set startOrderDate to 12:00 AM
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		startOrderDate = calendar.getTime();

		// Set startOrderDate to 12:00 AM
		calendar.set(Calendar.HOUR_OF_DAY, 3);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		searchPreOutboundHeader.setStartOrderDate(calendar.getTime());

		// Set endOrderDate to 11:59 PM
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		searchPreOutboundHeader.setEndOrderDate(calendar.getTime());

		log.info("StartCreatedOn ------> {}", searchPreOutboundHeader.getStartOrderDate());
		log.info("EndCreatedOn ------> {}", searchPreOutboundHeader.getEndOrderDate());

		searchPreOutboundHeader.setWarehouseId(Collections.singletonList("110"));
		PreOutboundHeader[] sortedPreOutboundHeaders = null;
		PreOutboundHeader[] sortedPreOutboundHeaders111 = null;

		// Generate the PDF report for WH_ID 110
		PreOutboundHeader[] preOutboundHeaders = transactionService.findPreOutboundHeaderPdf(searchPreOutboundHeader);
		if(preOutboundHeaders != null && preOutboundHeaders.length > 0) {
			sortedPreOutboundHeaders = Arrays.stream(preOutboundHeaders)
					.filter(n -> n.getRefDocDate() != null)
					.sorted(Comparator.comparing(PreOutboundHeader::getRefDocDate))
					.toArray(PreOutboundHeader[]::new);
		}

		searchPreOutboundHeader.setWarehouseId(Collections.singletonList("111"));

		// Generate the Pdf Report for WH_ID 111
		PreOutboundHeader[] preOutboundHeaders1 = transactionService.findPreOutboundHeaderPdf(searchPreOutboundHeader);
		if(preOutboundHeaders1 != null && preOutboundHeaders1.length > 0) {
			sortedPreOutboundHeaders111 = Arrays.stream(preOutboundHeaders1)
					.filter(n -> n.getRefDocDate() != null)
					.sorted(Comparator.comparing(PreOutboundHeader::getRefDocDate))
					.toArray(PreOutboundHeader[]::new);
		}

		File pdfFile = new File("WMS_Daily_Order_Report_110.pdf");
		try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
			reportService.exportEmail(fos, sortedPreOutboundHeaders, searchPreOutboundHeader, startOrderDate); // Adjust `export` to accept OutputStream
		}

		File pdfFile1 = new File("WMS_Daily_Order_Report_111.pdf");
		try (FileOutputStream fos = new FileOutputStream(pdfFile1)) {
			reportService.exportEmail(fos, sortedPreOutboundHeaders111, searchPreOutboundHeader, startOrderDate); // Adjust `export` to accept OutputStream
		}

		String fileName1 = "WMS_Daily_Order_Report_110.pdf";
		String fileName2 = "WMS_Daily_Order_Report_111.pdf";

		// Convert the File to MultipartFile
		try (FileInputStream fileInputStream = new FileInputStream(pdfFile)) {
			MultipartFile multipartFile = new MockMultipartFile(
					fileName1,                 // Original file name
					fileName1,                 // File name
					"application/pdf",        // Content type
					fileInputStream           // File content as InputStream
			);

			// Use the existing storeFile method
			fileStorageService.storeFile(multipartFile);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store the file.");
		}

		// Convert the File to MultipartFile
		try (FileInputStream fileInputStream = new FileInputStream(pdfFile1)) {
			MultipartFile multipartFile = new MockMultipartFile(
					fileName2,                 // Original file name
					fileName2,                 // File name
					"application/pdf",        // Content type
					fileInputStream           // File content as InputStream
			);

			// Use the existing storeFile method
			fileStorageService.storeFile(multipartFile);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store the file.");
		}

		sendMailService.sendTvReportMail(fileName1, fileName2);

		return ResponseEntity.ok("Email sent successfully: " + fileName1 + "and" + fileName2);
	}

	//PickerDenialReport
	@ApiOperation(response = PickerDenialReport.class, value = "Search PickerDenialReport") // label for swagger
	@PostMapping("/pickupline/findPickerDenialReport")
	public PickerDenialReport findPickerDenialReport(@RequestBody SearchPickupLine searchPickupLine) throws Exception {
		return transactionService.pickerDenialReport(searchPickupLine);

	}
}