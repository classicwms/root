package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.notification.OutputMessage;
import com.tekclover.wms.api.masters.model.notification.WebSocketNotification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class NotificationController {

    private SimpMessagingTemplate simpMessagingTemplate = null;

    public NotificationController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/notification")
    @SendTo("/topic/messages")
    public OutputMessage send(final WebSocketNotification message) throws Exception {

        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time, null, null);
    }

//    @Scheduled(fixedRate = 5000)
//    public void sendMessage() {
//        final String time = new SimpleDateFormat("HH:mm").format(new Date());
//        simpMessagingTemplate.convertAndSend("/topic/messages",
//                new OutputMessage("CRM Service", "Notification 1", time));
//    }

}