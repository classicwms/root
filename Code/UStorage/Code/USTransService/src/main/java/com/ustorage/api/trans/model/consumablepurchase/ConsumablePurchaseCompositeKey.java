package com.ustorage.api.trans.model.consumablepurchase;

import java.io.Serializable;

import lombok.Data;



@Data

public class ConsumablePurchaseCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;

	//"ITEM_CODE","RECEIPT_NO"


	private String itemCode;
	private String receiptNo;
}
