package com.tekclover.wms.core.model.masters;

import lombok.Data;
import java.util.Date;

@Data
public class NumberRangeStorageBin {

    private String companyCodeId;

    private String plantId;

    private String languageId;

    private String warehouseId;

    private Long floorId;

    private Long storageSectionId;

    private String rowId;

    private String aisleNumber;

    private String spanId;

    private String shelfId;

    private String numberRangeType;

    private String numberRangeFrom;

    private String numberRangeTo;

    private String currentNumberRange;

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
