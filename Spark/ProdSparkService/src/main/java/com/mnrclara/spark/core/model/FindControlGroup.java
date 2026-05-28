package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindControlGroup {

    private List<String> companyId;
    private List<String> languageId;
    private List<String> groupTypeId;
    private List<Long> versionNumber;
    private List<Long> statusId;
    private Date startCreatedOn;
    private Date endCreatedOn;
}
