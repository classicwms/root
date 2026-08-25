package com.tekclover.wms.api.idmaster.model.hhtuser;

import java.io.Serializable;

import lombok.Data;

@Data
public class HhtUserCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `WH_ID`, `USR_ID`
	 */
	private String warehouseId;
	private String userId;
}
