package com.mnrclara.spark.core.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;


@Data
public class ControlGroupType {

    private Long versionNumber;
    private String languageId;
    private String companyId;
    private String groupTypeId;
    private String groupTypeName;
    private Long statusId;
    private Timestamp validityDateFrom;
    private Timestamp validityDateTo;
    private Timestamp createdOn;
}
