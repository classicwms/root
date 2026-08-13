package com.tekclover.wms.api.masters.service;

import com.tekclover.wms.api.masters.config.PropertiesConfig;
import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.email.*;
import com.tekclover.wms.api.masters.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SendMailService {

    private static final String ACCESS_TOKEN = null;

    @Autowired
    private PropertiesConfig propertiesConfig;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EMailDetailsService eMailDetailsService;

    public void sendMail(OrderCancelInput orderCancelInput) throws MessagingException, IOException {
        //Send Email
        log.info("Send Mail Initiated " + new Date());

        List<EMailDetails> userEMail = eMailDetailsService.getEMailDetailsList();
        Set<String> toAddressList = new HashSet<>();
        Set<String> ccAddressList = new HashSet<>();

        if (userEMail == null || userEMail.isEmpty()) {
            throw new BadRequestException("Email Id is Empty");
        }

        if (userEMail != null && !userEMail.isEmpty()) {
            toAddressList = userEMail.stream().map(EMailDetails::getToAddress).collect(Collectors.toSet());
            ccAddressList = userEMail.stream().map(EMailDetails::getCcAddress).collect(Collectors.toSet());
        }
        String toAddress = "";
        String ccAddress = "";

        for (String dbToAddress : toAddressList) {

            if (dbToAddress != null) {
                toAddress = dbToAddress + "," + toAddress;
            }
        }
        for (String dbCcAddress : ccAddressList) {
            if (dbCcAddress != null) {
                ccAddress = dbCcAddress + "," + ccAddress;
            }
        }
        String localDate = DateUtils.getCurrentDateWithoutTimestamp();
        String c_id = orderCancelInput.getCompanyCodeId();
        String plant_id = orderCancelInput.getPlantId();
        String ref_doc_no = orderCancelInput.getRefDocNumber();
        String emailBodyText;
        String emailSubject;

//        if(orderCancelInput.getReferenceField1().equalsIgnoreCase("STOCKADJUSTMENT") ||
//                orderCancelInput.getReferenceField1().equalsIgnoreCase("ITEMMASTER")) {
//            emailBodyText = "Dear Almailem Operations/IT team,<br><br>" + "Please find below the failed order sync details" +
//                    "<br><br> Order No&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp : " + ref_doc_no +                              //7+9
//                    "<br><br> Mfr Name&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp : " + orderCancelInput.getReferenceField2() +   //7+9
//                    "<br> Middleware Table&nbsp : " + orderCancelInput.getReferenceField1() +                                       //15+1
//                    "<br> Company Code&nbsp&nbsp&nbsp&nbsp&nbsp : " + c_id +                                                        //11+5
//                    "<br> Branch Code&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp : " + plant_id +                                                //10+6
//                    "<br> Failure Reason&nbsp&nbsp&nbsp : " + orderCancelInput.getRemarks() +                                       //13+3
//                    "<br><br>Regards<br>Classic WMS Support Team";
//        } else {
//            emailBodyText = "Dear Almailem Operations/IT team,<br><br>" + "Please find below the failed order sync details" +
//                    "<br><br> Order No&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp : " + ref_doc_no +                   //7+9
//                    "<br> Middleware Table&nbsp : " + orderCancelInput.getReferenceField1() +                            //15+1
//                    "<br> Company Code&nbsp&nbsp&nbsp&nbsp&nbsp : " + c_id +                                             //11+5
//                    "<br> Branch Code&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp : " + plant_id +                                     //10+6
//                    "<br> Failure Reason&nbsp&nbsp&nbsp : " + orderCancelInput.getRemarks() +                           //13+3
//                    "<br><br>Regards<br>Classic WMS Support Team";
//        }

        if(orderCancelInput.getReferenceField1().equalsIgnoreCase("STOCKADJUSTMENT") ||
                orderCancelInput.getReferenceField1().equalsIgnoreCase("ITEMMASTER")) {

            emailSubject = "Classic WMS/AMS Integration - Sync failure Notification - Item Code: " + ref_doc_no + ", " + orderCancelInput.getReferenceField2() + ", " + localDate;

            emailBodyText = "Dear Walkaroo Operations/IT team,<br><br>" + "Please find below the failed order sync details <br><br> " +

                    "<table>"+
                    "<tr>"+
                    "<td>Item Code </td>"+
                    "<td>: " + ref_doc_no + "</td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td>Mfr Name </td>"+
                    "<td>: " + orderCancelInput.getReferenceField2() + "</td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td>Middleware Table </td>"+
                    "<td>: " + orderCancelInput.getReferenceField1() + "</td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td>Company Code </td>"+
                    "<td>: " + c_id + "</td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td>Branch Code </td>"+
                    "<td>: " + plant_id + "</td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td>Failure Reason </td>"+
                    "<td>: " + orderCancelInput.getRemarks() + "</td>"+
                    "</tr>"+
                    "</table>"+

                    "<br><br>Regards<br>Classic WMS Support Team";
        } else {
            emailSubject = "Classic WMS/AMS Integration - Sync failure Notification - Order No: " + ref_doc_no + ", " + localDate;
            emailBodyText = "Dear Walkaroo Operations/IT team,<br><br>" + "Please find below the failed order sync details" +
                    "<table>"+
                    "<tr>"+
                    "<td>Order No </td>"+
                    "<td>: " + ref_doc_no + "</td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td>Middleware Table </td>"+
                    "<td>: " + orderCancelInput.getReferenceField1() + "</td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td>Company Code </td>"+
                    "<td>: " + c_id + "</td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td>Branch Code </td>"+
                    "<td>: " + plant_id + "</td>"+
                    "</tr>"+
                    "<tr>"+
                    "<td>Failure Reason </td>"+
                    "<td>: " + orderCancelInput.getRemarks() + "</td>"+
                    "</tr>"+
                    "</table>"+
                    "<br><br>Regards<br>Classic WMS Support Team";
        }

        EMailDetails email = new EMailDetails();

        email.setSenderName("Walkaroo-Support");
        email.setSubject(emailSubject);
        email.setBodyText(emailBodyText);
        email.setToAddress(toAddress);
        email.setCcAddress(ccAddress);
        sendaMail(email);
    }

    /**
     * sendMail
     *
     * @param email
     * @throws MessagingException
     * @throws IOException
     */
    public void sendaMail(EMailDetails email) throws MessagingException, IOException {

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
            e.printStackTrace();
            helper.setFrom(propertiesConfig.getEmailFromAddress());
            helper.setTo("raj@tekclover.com");
            helper.setCc("senthil.v@tekclover.com");
            helper.setSubject("WK_Failed Order Details sending through eMail Failed");
            helper.setText("WK_Failed Order Details sending through eMail Failed", true);
            javaMailSender.send(msg);
            log.info("WK_Failed Order Detail Mail sent Unsuccessful");
            throw new BadRequestException("WK_Mail Sent Failed" + e);
        }
    }
}
