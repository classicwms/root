package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindImBasicData1 {

	private String companyCodeId;
    private String plantId;
    private String languageId;
    private String warehouseId;
    private List<String> itemCode;
    private String manufacturerName;
    private Date fromCreatedOn;
    private Date toCreatedOn;
}