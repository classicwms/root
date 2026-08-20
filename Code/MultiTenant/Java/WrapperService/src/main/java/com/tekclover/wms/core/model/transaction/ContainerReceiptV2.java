package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContainerReceiptV2 extends ContainerReceipt {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    private Date referenceField11;

    private Date referenceField12;

    private Date referenceField13;

    private Date referenceField14;

    private Date referenceField15;

    private Date referenceField16;

    private Date referenceField17;

    private Date referenceField18;

    private Date referenceField19;

    private Date referenceField20;

    private String referenceField21;

    private String referenceField22;

    private String referenceField23;

    private String referenceField24;

    private String referenceField25;

    private String referenceField26;

    private String referenceField27;

    private String referenceField28;

    private String referenceField29;

    private String referenceField30;

}
