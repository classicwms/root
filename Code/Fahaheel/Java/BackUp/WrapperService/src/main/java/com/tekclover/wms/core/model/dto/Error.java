package com.tekclover.wms.core.model.dto;

public class Error {


//    private int lineNo;

    private int lineNo;
    private String message;

    public Error(int lineNo, String message) {
        this.lineNo = lineNo;
        this.message = message;
    }

    public Error() {

    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{lineNo=" + lineNo + ", message=" + message + "}";
    }
}
