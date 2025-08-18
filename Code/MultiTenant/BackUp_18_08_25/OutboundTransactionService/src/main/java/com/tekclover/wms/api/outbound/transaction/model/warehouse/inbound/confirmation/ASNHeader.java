package com.tekclover.wms.api.outbound.transaction.model.warehouse.inbound.confirmation;

import lombok.Data;

@Data
public class ASNHeader {
	/*
	 * {
		 "asnHeader": {
			 	"asnNumber": "ASN000000580"
			 },
			 "asnLines": [
				 {
					 "sku": "026821038 ",
					 "skuDescription": "KRUGER COUGAR DIFFUSER 200ml",
					 "lineReference": 1,
					 "expectedQty": 2,
					 "uom": "PIECE",
					 "receivedQty": 2,
					 "damagedQty": 0,
					 "packQty": 2,
					 "actualReceiptDate": "05:05:2021",
					 "wareHouseId": "110"
				 }
			 ]
		 }
	 */
	
	private String asnNumber;
	private String supplierInvoice; // Fetch INV_NO from 1st record and insert
}
