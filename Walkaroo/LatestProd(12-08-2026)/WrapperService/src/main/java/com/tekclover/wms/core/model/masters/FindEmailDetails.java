package com.tekclover.wms.core.model.masters;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindEmailDetails {
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> languageId;
    private List<String> warehouseId;
    private List<String> createdBy;
    private List<String> senderName;
    private List<String> fromAddress;
    private List<String> toAddress;
    private List<String> ccAddress;
    private List<Long> deletionIndicator;
    private Date startDate;
    private Date endDate;
}