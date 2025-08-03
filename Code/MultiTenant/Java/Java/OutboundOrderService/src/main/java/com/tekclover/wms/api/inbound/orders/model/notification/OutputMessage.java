package com.tekclover.wms.api.inbound.orders.model.notification;

import lombok.Data;

@Data
public class OutputMessage {

    private String from;
    private String text;
    private String time;
    private String userId;
    private String userType;

    /**
     * 
     * @param from
     * @param text
     * @param time
     * @param userId
     * @param userType
     */
    public OutputMessage(final String from, final String text, final String time,final String userId,final String userType) {
        this.from = from;
        this.text = text;
        this.time = time;
        this.userId = userId;
        this.userType = userType;
    }
}
