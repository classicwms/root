package com.tekclover.wms.api.idmaster.model.hhtuser;

import lombok.Data;

import java.util.List;

@Data
public class FindHhtUser {
    private List<String> warehouseId;
    private List<String> userId;
    private List<String> companyCodeId;
    private List<String> languageId;
    private List<String> plantId;
    private List<Long> levelId;
    private List<String> userPresent;
    private List<String> noOfDaysLeave;

}