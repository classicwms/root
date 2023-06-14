package com.tekclover.wms.api.idmaster.service;

import com.tekclover.wms.api.idmaster.config.PropertiesConfig;
import com.tekclover.wms.api.idmaster.controller.exception.BadRequestException;
import com.tekclover.wms.api.idmaster.model.email.*;
import com.tekclover.wms.api.idmaster.repository.FileNameForEmailRepository;
import com.tekclover.wms.api.idmaster.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

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

		List<EMailDetails> userEMail = eMailDetailsService.getEMailDetailsList();

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
}
