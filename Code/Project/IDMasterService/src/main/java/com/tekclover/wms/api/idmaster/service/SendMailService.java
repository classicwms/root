package com.tekclover.wms.api.idmaster.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.idmaster.config.PropertiesConfig;
import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.email.EMailDetails;
import com.tekclover.wms.api.idmaster.model.email.FileNameForEmail;
import com.tekclover.wms.api.idmaster.model.email.OrderFailedInput;
import com.tekclover.wms.api.idmaster.repository.FileNameForEmailRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendMailService {
	
	private static final String ACCESS_TOKEN = null;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private PropertiesConfig propertiesConfig;
	
	@Autowired
	private EMailDetailsService eMailDetailsService;
	
	@Autowired
	private FileNameForEmailService fileNameForEmailService;
	
	@Autowired
	private FileNameForEmailRepository fileNameForEmailRepository;

	@Scheduled(cron = "0 0 15 * * ?")
	public void sendMail() throws MessagingException, IOException {
		//Send Email
		log.info("Scheduling the Mail Started at "+ new Date());

		List<EMailDetails> userEMail = eMailDetailsService.getDailyReportEMailDetailsList();

		String toAddress = "";
		String ccAddress = "";

		for(EMailDetails eMailDetails: userEMail){

			if(eMailDetails.getToAddress()!=null) {
				toAddress = eMailDetails.getToAddress() + "," + toAddress;
			}

			if(eMailDetails.getCcAddress()!=null) {
				ccAddress = eMailDetails.getCcAddress() + "," + ccAddress;
			}
		}
		String localDate = DateUtils.getCurrentDateWithoutTimestamp();

		FileNameForEmail fileNameForEmail = fileNameForEmailService.getFileNameForEmailByDate(localDate);

		EMailDetails email = new EMailDetails();

		email.setSenderName("IWE Express-Support");
		email.setSubject("GRC - Amghara - Daily Shipment Delivery Report - "+localDate);
		email.setBodyText("Dear GRC Team,<br><br>"+"Please find the attached shipment delivery report for your reference<br><br>Regards<br>Operations Team - InnerWorks");
		email.setToAddress(toAddress);
		email.setCcAddress(ccAddress);
		sendMail(email,fileNameForEmail);
	}

	/**
	 * sendMail
	 * @param email
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void sendMail (EMailDetails email, FileNameForEmail fileNameForEmail) throws MessagingException, IOException {

		MimeMessage msg = javaMailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(msg, true);

		String filePath1,filePath2,filePath3,filePath4;
		String filePath = propertiesConfig.getDocStorageBasePath()+propertiesConfig.getDocStorageDocumentPath()+"/";

		if(fileNameForEmail.getDelivery110()  != null &&
//				fileNameForEmail.getDispatch110() != null &&
				fileNameForEmail.getDelivery111() != null
//				fileNameForEmail.getDispatch111()!= null
		) {

			filePath1 = filePath + fileNameForEmail.getDelivery110();
//			filePath2 = filePath + fileNameForEmail.getDispatch110();
			filePath3 = filePath + fileNameForEmail.getDelivery111();
//			filePath4 = filePath + fileNameForEmail.getDispatch111();

			log.info("110 Delivery file Name: " + fileNameForEmail.getDelivery110());
//			log.info("110 Dispatch file Name: " + fileNameForEmail.getDispatch110());
			log.info("111 Delivery file Name: " + fileNameForEmail.getDelivery111());
//			log.info("111 Dispatch file Name: " + fileNameForEmail.getDispatch111());

			File file = new File(filePath1);
//			File file2 = new File(filePath2);
			File file3 = new File(filePath3);
//			File file4 = new File(filePath4);

			Path path = Paths.get(file.getAbsolutePath());
//			Path path2 = Paths.get(file2.getAbsolutePath());
			Path path3 = Paths.get(file3.getAbsolutePath());
//			Path path4 = Paths.get(file4.getAbsolutePath());

			ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
//			ByteArrayResource resource2 = new ByteArrayResource(Files.readAllBytes(path2));
			ByteArrayResource resource3 = new ByteArrayResource(Files.readAllBytes(path3));
//			ByteArrayResource resource4 = new ByteArrayResource(Files.readAllBytes(path4));

			helper.addAttachment(fileNameForEmail.getDelivery110(), resource);
//			helper.addAttachment(fileNameForEmail.getDispatch110(), resource2);
			helper.addAttachment(fileNameForEmail.getDelivery111(), resource3);
//			helper.addAttachment(fileNameForEmail.getDispatch111(), resource4);


			log.info("helper (From Address): " + email.getFromAddress());

			// Set From
			if (email.getFromAddress() != null && email.getFromAddress().isEmpty()) {
				helper.setFrom(email.getFromAddress());
			} else {
				helper.setFrom(propertiesConfig.getEmailFromAddress());
			}

//		helper.setTo(email.getToAddress());
			helper.setTo(InternetAddress.parse(email.getToAddress()));

			log.info("Email: To Address- " + email.getToAddress());

			if (email.getCcAddress() != null) {

				helper.setCc(InternetAddress.parse(email.getCcAddress()));

				log.info("Email: Cc Address- " + email.getCcAddress());

			} else {
				helper.setCc(InternetAddress.parse(email.getToAddress()));
			}

			helper.setSubject(email.getSubject());

			// true = text/html
			helper.setText(email.getBodyText(), true);
//			Long flag = 0L;
//			if(fileNameForEmail.getMailSent() != null) {
//				flag = Long.valueOf(fileNameForEmail.getMailSent());
//			}
//			if(flag == 1) {
//				throw new BadRequestException("Today's Report Mail has been sent already");
//			}else{
				javaMailSender.send(msg);
			log.info("Scheduled Mail sent successful");
//				fileNameForEmail.setMailSent("1");
				fileNameForEmailRepository.save(fileNameForEmail);
//			}
		}else {
			helper.setFrom(propertiesConfig.getEmailFromAddress());
			helper.setTo("raj@tekclover.com");
			helper.setCc("senthil.v@tekclover.com");
			helper.setSubject("Sending Report Through eMail Failed");
			helper.setText("Attachment not found, Sending Report Through eMail Failed", true);
//			Long flag = 0L;
//			if (fileNameForEmail.getMailSentFailed() != null) {
//				flag = Long.valueOf(fileNameForEmail.getMailSentFailed());
//			}
//			if (flag == 1) {
//				throw new BadRequestException("Today's Report Mail Failed Message Has been sent already");
//			} else {
				javaMailSender.send(msg);
				log.info("Scheduled Mail sent Unsuccessful");
//				fileNameForEmail.setMailSentFailed("1");
				fileNameForEmailRepository.save(fileNameForEmail);
//			}
			throw new MessagingException("Attachment not found, Sending email failed");
		}
	}


	public void sendTvReportMail(String fileName, String fileName2) throws MessagingException, IOException {

		//Send Email
		log.info("Scheduling the TV Report Mail Started at "+ new Date());

		List<EMailDetails> userEMail = eMailDetailsService.getReportEMailDetailsList();

		String toAddress = "";
		String ccAddress = "";

		for(EMailDetails eMailDetails: userEMail){

			if(eMailDetails.getToAddress()!=null) {
				toAddress = eMailDetails.getToAddress() + "," + toAddress;
			}

			if(eMailDetails.getCcAddress()!=null) {
				ccAddress = eMailDetails.getCcAddress() + "," + ccAddress;
			}
		}
		String localDate = DateUtils.getCurrentDateWithoutTimestamp();
		String emailSubject = "WMS Daily Order Report - True Value and True Express - "+localDate;

//		FileNameForEmail fileNameForEmail = fileNameForEmailService.getFileNameForEmailByDate(localDate);

		EMailDetails email = new EMailDetails();

		email.setSenderName("IWE Express-Support");
		email.setSubject(emailSubject);
		email.setBodyText("Dear IW Express team,<br><br>"+"Please find the attached WMS Daily Order Report for your reference<br><br>Regards<br>WMS IT Team");
		email.setToAddress(toAddress);
		email.setCcAddress(ccAddress);
		sendTvReportMail(email,fileName, fileName2);
	}

	/**
	 * sendMail
	 * @param email
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void sendTvReportMail (EMailDetails email, String fileNameForEmail, String fileName2ForEmail) throws MessagingException, IOException {

		MimeMessage msg = javaMailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(msg, true);

		String filePath1;
		String filePath = propertiesConfig.getDocStorageBasePath() + "/";

		String filePath2;
		String filePath3 = propertiesConfig.getDocStorageBasePath() + "/";

		if(!fileNameForEmail.isEmpty() && !fileName2ForEmail.isEmpty()) {

			filePath1 = filePath + fileNameForEmail;
			filePath2 = filePath3 + fileName2ForEmail;

			log.info("110 Order Report File Name: " + fileNameForEmail);
			log.info("111 Order Report File Name: " + fileName2ForEmail);

			File file = new File(filePath1);
			File file2 = new File(filePath2);

			Path path = Paths.get(file.getAbsolutePath());
			Path path1 = Paths.get(file2.getAbsolutePath());

			ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
			ByteArrayResource resource1 = new ByteArrayResource(Files.readAllBytes(path1));

			helper.addAttachment(fileNameForEmail, resource);
			helper.addAttachment(fileName2ForEmail, resource1);


			log.info("helper (From Address): " + email.getFromAddress());

			// Set From
			if (email.getFromAddress() != null && email.getFromAddress().isEmpty()) {
				helper.setFrom(email.getFromAddress());
			} else {
				helper.setFrom(propertiesConfig.getEmailFromAddress());
			}

			helper.setTo(InternetAddress.parse(email.getToAddress()));

			log.info("Email: To Address- " + email.getToAddress());

			if (email.getCcAddress() != null) {

				helper.setCc(InternetAddress.parse(email.getCcAddress()));

				log.info("Email: Cc Address- " + email.getCcAddress());

			} else {
				helper.setCc(InternetAddress.parse(email.getToAddress()));
			}

			helper.setSubject(email.getSubject());

			helper.setText(email.getBodyText(), true);

			javaMailSender.send(msg);
			log.info("Report - Scheduled Mail sent successful---> " + fileNameForEmail);
		}else {
			helper.setFrom(propertiesConfig.getEmailFromAddress());
			helper.setTo("raj@tekclover.com");
			helper.setCc("senthil.v@tekclover.com");
			String subject = propertiesConfig.getEmailSubject()+"TV-Sending Report Through eMail Failed";
			helper.setSubject(subject);
			helper.setText("Attachment not found, Sending Report Through eMail Failed", true);
			javaMailSender.send(msg);
			log.info("Scheduled Mail sent Unsuccessful ----> " + fileNameForEmail);
			throw new MessagingException("Attachment not found, Sending email failed");
		}
	}


	/**
	 *
	 * @param orderCancelInput
	 * @throws Exception
	 */
	public void sendMail(OrderFailedInput orderCancelInput) throws Exception {
		//Send Email
        try {
            log.info("Send Mail Initiated " + new Date());
            String localDate = DateUtils.getCurrentDateWithoutTimestamp();
			EMailDetails email = geteMailDetails(orderCancelInput, localDate);
			sendMail(email);
        } catch (Exception e) {
			log.error("Exception while sending order process failed mail : " + e.toString());
            throw e;
        }
    }

	/**
	 *
	 * @param orderCancelInput
	 * @param localDate
	 * @return
	 */
	private static EMailDetails geteMailDetails(OrderFailedInput orderCancelInput, String localDate) {
		String wh_id = orderCancelInput.getWarehouseId();
		String ref_doc_no = orderCancelInput.getRefDocNumber();
		String emailBodyText;
		String emailSubject;
		String toAddress = "yogesh.m@tekclover.com,fahima.a@tekclover.com";
		String ccAddress = "senthil.v@tekclover.com";

//		List<EMailDetails> userEMail = eMailDetailsService.getEMailDetailsList();
//		Set<String> toAddressList = new HashSet<>();
//		Set<String> ccAddressList = new HashSet<>();
//
//		if (userEMail == null || userEMail.isEmpty()) {
//			throw new BadRequestException("Email Id is Empty");
//		}
//
//		if (userEMail != null && !userEMail.isEmpty()) {
//			toAddressList = userEMail.stream().map(EMailDetails::getToAddress).collect(Collectors.toSet());
//			ccAddressList = userEMail.stream().map(EMailDetails::getCcAddress).collect(Collectors.toSet());
//		}
//		String toAddress = "";
//		String ccAddress = "";
//
//		for (String dbToAddress : toAddressList) {
//
//			if (dbToAddress != null) {
//				toAddress = dbToAddress + "," + toAddress;
//			}
//		}
//		for (String dbCcAddress : ccAddressList) {
//			if (dbCcAddress != null) {
//				ccAddress = dbCcAddress + "," + ccAddress;
//			}
//		}

		emailSubject = "Classic WMS/MSD Integration - Sync failure Notification - Order No: " + ref_doc_no + ", " + localDate;
		emailBodyText = "Dear IWExpress Operations/IT team,<br><br>" + "Please find below the failed order sync details" +
				"<table>"+
				"<tr>"+
				"<td>Order No </td>"+
				"<td>: " + ref_doc_no + "</td>"+
				"</tr>"+
				"<tr>"+
				"<td>Order Type Id </td>"+
				"<td>: " + orderCancelInput.getReferenceField1() + "</td>"+
				"</tr>"+
				"<tr>"+
				"<td>Warehouse </td>"+
				"<td>: " + wh_id + "</td>"+
				"</tr>"+
				"<tr>"+
				"<td>Failure Reason </td>"+
				"<td>: " + orderCancelInput.getRemarks() + "</td>"+
				"</tr>"+
				"</table>"+
				"<br><br>Regards<br>Classic WMS Support Team";

		EMailDetails email = new EMailDetails();

		email.setSenderName("Tekclover-Support");
		email.setSubject(emailSubject);
		email.setBodyText(emailBodyText);
		email.setToAddress(toAddress);
		email.setCcAddress(ccAddress);
		return email;
	}

	/**
	 * Order Process Failed Mail
	 * @param email
	 * @throws Exception
	 */
	public void sendMail(EMailDetails email) throws Exception {

		MimeMessage msg = null;
		MimeMessageHelper helper = null;
		try {
			msg = javaMailSender.createMimeMessage();
			helper = new MimeMessageHelper(msg, true);
			log.info("helper (From Address): " + email.getFromAddress());

			// Set From
			if (email.getFromAddress() != null && email.getFromAddress().isEmpty()) {
				helper.setFrom(email.getFromAddress());
			} else {
				helper.setFrom(propertiesConfig.getEmailFromAddress());
			}

			helper.setTo(InternetAddress.parse(email.getToAddress()));

			log.info("Email: To Address- " + email.getToAddress());

			if (email.getCcAddress() != null) {

				helper.setCc(InternetAddress.parse(email.getCcAddress()));

				log.info("Email: Cc Address- " + email.getCcAddress());

			} else {
				helper.setCc(InternetAddress.parse(email.getToAddress()));
			}

			helper.setSubject(email.getSubject());

			// true = text/html
			helper.setText(email.getBodyText(), true);
			javaMailSender.send(msg);
			log.info("Failed Order Detail Mail sent successful");
		} catch (Exception e) {
			helper.setFrom(propertiesConfig.getEmailFromAddress());
			helper.setTo("yogesh.m@tekclover.com");
			helper.setCc("senthil.v@tekclover.com");
			helper.setSubject("Failed Order Details sending through eMail Failed");
			helper.setText("Failed Order Details sending through eMail Failed", true);
			javaMailSender.send(msg);
			log.info("Failed Order Detail Mail sent Unsuccessful");
			throw new BadRequestException("Mail Sent Failed" + e.toString());
		}
	}

	/**
	 *
	 * @param fileName110
	 * @param fileName111
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void sendPickerDenialReportMail(String fileName110, String fileName111) throws MessagingException, IOException {

		//Send Email
		log.info("Scheduling the TV Picker Denial Report Mail Started at "+ new Date());

		List<EMailDetails> userEMail = eMailDetailsService.getReportEMailDetailsList();

		String toAddress = "";
		String ccAddress = "";

		for(EMailDetails eMailDetails: userEMail){

			if(eMailDetails.getToAddress()!=null) {
				toAddress = eMailDetails.getToAddress() + "," + toAddress;
			}

			if(eMailDetails.getCcAddress()!=null) {
				ccAddress = eMailDetails.getCcAddress() + "," + ccAddress;
			}
		}
		String localDate = DateUtils.getCurrentDateWithoutTimestamp();
		String emailSubject = "WMS PickerDenial Report - True Value and True Express - "+localDate;

		EMailDetails email = new EMailDetails();

		email.setSenderName("IWE Express-Support");
		email.setSubject(emailSubject);
		email.setBodyText("Dear IW Express team,<br><br>"+"Please find the attached WMS Picker Denial Report for your reference<br><br>Regards<br>WMS IT Team");
		email.setToAddress(toAddress);
		email.setCcAddress(ccAddress);
		sendTvReportMail(email,fileName110, fileName111);
	}

	/**
	 * Method for Sending the File in Email
	 * Aakash Vinayak - 04/07/2025
	 *
	 * @param fileName110
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void sendShipmentDeliveryReport(String fileName110) throws MessagingException, IOException {

		//Send Email
		log.info("Scheduling the TV Shipment Delivery Report Mail Started at "+ new Date());

		List<EMailDetails> userEMail = eMailDetailsService.getReportEMailDetailsList();

		String toAddress = "";
		String ccAddress = "";

		for(EMailDetails eMailDetails: userEMail){
			if(eMailDetails.getToAddress()!=null) {
				toAddress = eMailDetails.getToAddress() + "," + toAddress;
			}

			if(eMailDetails.getCcAddress()!=null) {
				ccAddress = eMailDetails.getCcAddress() + "," + ccAddress;
			}
		}
		
		String localDate = DateUtils.getCurrentDateWithoutTimestamp();
		String emailSubject = "WMS Shipment Delivery Report - True Value and True Express - "+localDate;

		EMailDetails email = new EMailDetails();

		email.setSenderName("IWE Express-Support");
		email.setSubject(emailSubject);
		email.setBodyText("Dear IW Express team,<br><br>"+"Please find the attached WMS Shipment Delivery Report for your reference<br><br>Regards<br>WMS IT Team");
		email.setToAddress(toAddress);
		email.setCcAddress(ccAddress);
		sendShipmentDeliveryReport(email,fileName110);
	}

	/**
	 * sendMail attachement shipmentDeliveryReport generated pdf
	 * Aakash Vinayak - 04/07/2025
	 *
	 * @param email
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void sendShipmentDeliveryReport (EMailDetails email, String fileNameForEmail) throws MessagingException, IOException {

		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);

		String basePath = propertiesConfig.getDocStorageBasePath() + "/";
		String filePath = basePath + fileNameForEmail;

		if (fileNameForEmail != null && !fileNameForEmail.isEmpty()) {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new MessagingException("Attachment file not found: " + filePath);
			}

			Path path = Paths.get(file.getAbsolutePath());
			ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

			helper.addAttachment(fileNameForEmail, resource);

			// Set From Address
			if (email.getFromAddress() != null && !email.getFromAddress().isEmpty()) {
				helper.setFrom(email.getFromAddress());
			} else {
				helper.setFrom(propertiesConfig.getEmailFromAddress());
			}

			// Set To Address
			helper.setTo(InternetAddress.parse(email.getToAddress()));
			log.info("Email: To Address - " + email.getToAddress());

			// Set CC Address
			if (email.getCcAddress() != null && !email.getCcAddress().isEmpty()) {
				helper.setCc(InternetAddress.parse(email.getCcAddress()));
				log.info("Email: Cc Address - " + email.getCcAddress());
			} else {
				helper.setCc(InternetAddress.parse(email.getToAddress()));
			}

			helper.setSubject(email.getSubject());
			helper.setText(email.getBodyText(), true);

			javaMailSender.send(msg);
			log.info("Scheduled mail sent successfully with attachment: " + fileNameForEmail);

		} else {
			// Fallback: Attachment missing
			helper.setFrom(propertiesConfig.getEmailFromAddress());
			helper.setTo("raj@tekclover.com");
			helper.setCc("senthil.v@tekclover.com");
			helper.setSubject(propertiesConfig.getEmailSubject() + "TV - Sending Report Through Email Failed");
			helper.setText("Attachment not found. Sending Report Through Email Failed.", true);
			javaMailSender.send(msg);

			log.info("Scheduled mail failed due to missing attachment: " + fileNameForEmail);
			throw new MessagingException("Attachment not found. Email send failed.");
		}
	}
}