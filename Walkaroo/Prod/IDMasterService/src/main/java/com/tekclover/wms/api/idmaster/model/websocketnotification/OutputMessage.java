package com.tekclover.wms.api.idmaster.model.websocketnotification;

public class OutputMessage {

    private String from;
    private String text;
    private String time;
    private String userId;
    private String userType;

    public OutputMessage(final String from, final String text, final String time,final String userId,final String userType) {

        this.from = from;
        this.text = text;
        this.time = time;
        this.userId = userId;
        this.userType = userType;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public String getFrom() {
        return from;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }
}
