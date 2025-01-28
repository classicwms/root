package com.tekclover.wms.core.service;

import com.tekclover.wms.core.config.PropertiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PropertiesConfig propertiesConfig;

    /**
     * Email TV pdf report
     *
     * @param toEmail
     * @param subject
     * @param body
     * @param attachment
     * @throws Exception
     */
    public void sendEmailWithAttachment(String toEmail, String subject, String body, File attachment) throws Exception {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(propertiesConfig.getEmailFromAddress());
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            // Add attachment
            helper.addAttachment(attachment.getName(), attachment);

            javaMailSender.send(message);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
