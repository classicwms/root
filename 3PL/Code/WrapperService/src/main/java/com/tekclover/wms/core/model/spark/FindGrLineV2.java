package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FindGrLineV2 {

    private List<String> languageId;
    private List<String> companyCodeId;
    private List<String> plantId;
    private List<String> warehouseId;
    private List<String> manufacturerCode;
    private List<String> barcodeId;
    private List<String> manufacturerName;
    private List<String> origin;
    private List<String> brand;
    private List<String> preInboundNo;
    private List<String> refDocNumber;
    private List<String> caseCode;
    private List<Long> lineNo;
    private List<String> itemCode;
    private List<Long> inboundOrderTypeId;
    private List<Long> statusId;
    private List<String> interimStorageBin;
    private List<String> rejectType;
    private List<String> rejectReason;
    private List<String> packBarcodes;
    private Date startCreatedOn;
    private Date endCreatedOn;
    /*--------------------------3PL--------------------------*/
    private List<Double> threePLCbm;
    private List<String> threePLUom;
    private List<String> threePLBillStatus;
    private List<Double> threePLLength;
    private List<Double> threePLHeight;
    private List<Double> threePLWidth;
}
