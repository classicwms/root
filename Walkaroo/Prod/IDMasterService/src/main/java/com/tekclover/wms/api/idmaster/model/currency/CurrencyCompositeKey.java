package com.tekclover.wms.api.idmaster.model.currency;

import lombok.Data;

import java.io.Serializable;

@Data
public class CurrencyCompositeKey implements Serializable {

    private static final long serialVersionUID = -7617672247680004647L;

	/*
	 * `CURR_ID`,`LANG_ID`

	 */
    private Long currencyId;
    private String languageId;
}
