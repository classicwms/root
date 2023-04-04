package com.tekclover.wms.core.service;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.idmaster.EMailDetails;
import com.tekclover.wms.core.model.idmaster.FileNameForEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DocStorageService {
	
	private static final String ACCESS_TOKEN = null;
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	PropertiesConfig propertiesConfig;

	/**
	 * 
	 * @param location
	 * @param file
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public String getQualifiedFilePath (String location, String file) throws Exception {
		String filePath = propertiesConfig.getDocStorageBasePath();
		
		log.info("getQualifiedFilePath---location------>: " + location);
		log.info("getQualifiedFilePath---file------>: " + file);
		
		try {
			if (location != null && location.startsWith("document")) {
				filePath = filePath + propertiesConfig.getDocStorageDocumentPath();

				log.info("filePath------in document------>: " + filePath);
			}
		} catch (Exception e) {
			log.info("getQualifiedFilePath------Error------>: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		filePath = filePath + "/" + file;
		log.info("filePath: " + filePath);
		return filePath;
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

		if(fileNameForEmail.getDelivery110()!=null&&fileNameForEmail.getDispatch110()!=null&&
				fileNameForEmail.getDelivery111()!=null&&fileNameForEmail.getDispatch111()!=null) {
			filePath1 = filePath + fileNameForEmail.getDelivery110();
			filePath2 = filePath + fileNameForEmail.getDispatch110();
			filePath3 = filePath + fileNameForEmail.getDelivery111();
			filePath4 = filePath + fileNameForEmail.getDispatch111();

			File file = new File(filePath1);
			File file2 = new File(filePath2);
			File file3 = new File(filePath3);
			File file4 = new File(filePath4);
			Path path = Paths.get(file.getAbsolutePath());
			Path path2 = Paths.get(file2.getAbsolutePath());
			Path path3 = Paths.get(file3.getAbsolutePath());
			Path path4 = Paths.get(file4.getAbsolutePath());
			ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
			ByteArrayResource resource2 = new ByteArrayResource(Files.readAllBytes(path2));
			ByteArrayResource resource3 = new ByteArrayResource(Files.readAllBytes(path3));
			ByteArrayResource resource4 = new ByteArrayResource(Files.readAllBytes(path4));
			helper.addAttachment(fileNameForEmail.getDelivery110(), resource);
			helper.addAttachment(fileNameForEmail.getDispatch110(), resource2);
			helper.addAttachment(fileNameForEmail.getDelivery111(), resource3);
			helper.addAttachment(fileNameForEmail.getDispatch111(), resource4);

			// true = multipart message

			log.info("helper : " + email.getFromAddress());

			// Set From
			if (email.getFromAddress() != null && email.getFromAddress().isEmpty()) {
				helper.setFrom(email.getFromAddress());
			} else {
				helper.setFrom(propertiesConfig.getEmailFromAddress());
			}

//		helper.setTo(email.getToAddress());
			helper.setTo(InternetAddress.parse(email.getToAddress()));
			if (email.getCcAddress() != null) {
				helper.setCc(InternetAddress.parse(email.getCcAddress()));
			} else {
				helper.setCc(InternetAddress.parse(email.getToAddress()));
			}

			helper.setSubject(email.getSubject());

			// true = text/html
			helper.setText(email.getBodyText(), true);

			javaMailSender.send(msg);
		}else{
			helper.setFrom(propertiesConfig.getEmailFromAddress());
			helper.setTo("raj@tekclover.com");
			helper.setCc("senthil.v@tekclover.com");
			helper.setSubject("Sending Report Through eMail Failed");
			helper.setText("Attachment not found, Sending Report Through eMail Failed", true);
			javaMailSender.send(msg);
			throw new MessagingException("Attachment not found, Sending email failed");
		}
	}
}
