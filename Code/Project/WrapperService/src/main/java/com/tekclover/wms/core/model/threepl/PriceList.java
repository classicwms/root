package com.tekclover.wms.core.model.threepl;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
@Data
public class PriceList {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String moduleId;
    private Long priceListId;
    private Long serviceTypeId;
    private Long chargeRangeId;
    private Date fromPeriod;
    private Date toPeriod;
    private Double chargeRangeFrom;
    private Double chargeRangeTo;
    private String chargeUnit;
    private Double pricePerChargeUnit;
    private String priceUnit;
    private Double minMonthlyPrice;
    private Long statusId;
    private String companyIdAndDescription;
    private String plantIdAndDescription;
    private String warehouseIdAndDescription;
    private String moduleIdAndDescription;
    private String serviceTypeIdAndDescription;
    private String description;

    private Long deletionIndicator;
    private String referenceField1;
    private String referenceField2;
    private String referenceField3;
    private String referenceField4;
    private String referenceField5;
    private String referenceField6;
    private String referenceField7;
    private String referenceField8;
    private String referenceField9;
    private String referenceField10;
    private String createdBy;
    private Date createdOn = new Date();
    private String updatedBy;
    private Date updatedOn = new Date();
}
