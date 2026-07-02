package com.tekclover.wms.core.model.threepl;

import lombok.Data;
import java.util.Date;
@Data
public class AddPriceList {
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
}
