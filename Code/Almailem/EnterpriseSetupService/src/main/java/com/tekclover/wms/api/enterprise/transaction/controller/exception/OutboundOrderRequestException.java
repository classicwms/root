package com.tekclover.wms.api.enterprise.transaction.controller.exception;

public class OutboundOrderRequestException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OutboundOrderRequestException(String message){
        super(message);
    }
}