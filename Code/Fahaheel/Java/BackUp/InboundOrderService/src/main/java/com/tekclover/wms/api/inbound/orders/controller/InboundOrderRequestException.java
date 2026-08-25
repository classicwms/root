package com.tekclover.wms.api.inbound.orders.controller;

public class InboundOrderRequestException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InboundOrderRequestException(String message){
        super(message);
    }
}
