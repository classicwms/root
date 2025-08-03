//package com.tekclover.wms.api.inbound.orders.service;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
//import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundOrderCancelInput;
//import com.tekclover.wms.api.inbound.orders.repository.PickupHeaderV2Repository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Iterator;
//import java.util.List;
//
//@Slf4j
//@Service
//public class PushNotificationService {
//    @Autowired
//    FirebaseMessaging firebaseMessaging;
//
//    @Autowired
//    MastersService mastersService;
//
//    @Autowired
//    PickupHeaderV2Repository pickupHeaderV2Repository;
//
//
//    /**
//     *
//     * @param tokens token
//     * @param title title
//     * @param message message
//     * @return return message
//     */
//    public String sendPushNotification(List<String> tokens, String title, String message) throws FirebaseMessagingException {
//
//        Iterator<String> iterator = tokens.iterator();
//        boolean emailSent = false;
//        while (iterator.hasNext()) {
//            String token = iterator.next();
//            if (token == null || token.isEmpty()) {
//                iterator.remove();
//                continue;
//            }
//            try {
//                Message pushMessage = Message.builder()
//                        .setToken(token)
//                        .setNotification(Notification.builder()
//                                .setTitle(title)
//                                .setBody(message)
//                                .build())
//                        .build();
//                firebaseMessaging.send(pushMessage);
//            } catch (FirebaseMessagingException e) {
//                iterator.remove();
//                if (e.getMessage().contains("invalid_grant") && !emailSent) {
//                    sendEmail("Firebase Key Expired: " + e.getMessage());
//                    emailSent = true;
//                }
//                log.error("FireBase Exception : " + e);
//            } catch (Exception e) {
//                log.error("Exception while push notification : " + e);
//            }
//        }
//        return "OK";
//    }
//
//    /**
//     *
//     * @param preOutboundNo getToken pass preInbound_no
//     * @param warehouseId warehouseId
//     */
//    public void sendPushNotification(String preOutboundNo, String warehouseId) {
//        try {
//            List<IKeyValuePair> notification =
//                    pickupHeaderV2Repository.findPushNotificationStatusByPreOutboundNo(preOutboundNo, warehouseId);
//
//            if (notification != null) {
//                for (IKeyValuePair pickupHeaderV2 : notification) {
//
//                    List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(
//                            pickupHeaderV2.getAssignPicker(), pickupHeaderV2.getWarehouseId());
//
//                    if (deviceToken != null && !deviceToken.isEmpty()) {
//                        String title = "PICKING";
//                        String message = pickupHeaderV2.getRefDocType() + " ORDER - " + pickupHeaderV2.getRefDocNumber() + " - IS RECEIVED ";
//                        String response = sendPushNotification(deviceToken, title, message);
//                        if (response.equals("OK")) {
//                            pickupHeaderV2Repository.updateNotificationStatus(
//                                    pickupHeaderV2.getAssignPicker(), pickupHeaderV2.getRefDocNumber(), pickupHeaderV2.getWarehouseId());
//                            log.info("status update successfully");
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.info("FireBase Exception");
//        }
//    }
//
//    // Send Mail
//    public void sendEmail(String message) {
//        InboundOrderCancelInput input = new InboundOrderCancelInput();
//        input.setRemarks(message);
//        mastersService.sendMailForNotification(input);
//    }
//}