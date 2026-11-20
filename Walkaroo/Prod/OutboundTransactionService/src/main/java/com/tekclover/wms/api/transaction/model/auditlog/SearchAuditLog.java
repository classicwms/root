package com.tekclover.wms.api.transaction.model.auditlog;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class SearchAuditLog {

    private List<String> languageId;

    private List<String> companyCodeId;

    private List<String> plantId;

    private List<String> warehouseId;

    private List<String> auditFileNumber;
    private List<Long> auditLogNumber;

    private List<Long> financialYear;
    private List<String> objectName;
    private List<String> modifiedTableName;
    private Date startCreatedOn;
    private Date endCreatedOn;
}
