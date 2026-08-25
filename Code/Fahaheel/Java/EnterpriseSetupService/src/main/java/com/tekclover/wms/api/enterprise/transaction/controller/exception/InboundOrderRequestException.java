package com.tekclover.wms.api.enterprise.transaction.controller.exception;

public class InboundOrderRequestException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InboundOrderRequestException(String message){
        super(message);
    }
}