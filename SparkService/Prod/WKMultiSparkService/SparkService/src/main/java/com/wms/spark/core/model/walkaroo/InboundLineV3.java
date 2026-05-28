package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class InboundLineV3 {

    private String languageId;
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String refDocNumber;
    private String preInboundNo;
    private Long lineNo;
    private String itemCode;
    private Double orderQty;
    private String orderUom;
    private Double acceptedQty;
    private Double damageQty;
    private Double putawayConfirmedQty;
    private Double varianceQty;
    private Long variantCode;
    private String variantSubCode;
    private Long inboundOrderTypeId;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String referenceOrderNo;
    private Long statusId;
    private String vendorCode;
    private Timestamp expectedArrivalDate;
    private String containerNo;
    private String invoiceNo;
    private String description;
    private String manufacturerPartNo;
    private String hsnCode;
    private String itemBarcode;
    private Double itemCaseQty;
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
    private Long deletionIndicator;
    private String createdBy;
    private Timestamp createdOn;
    private String updatedBy;
    private Timestamp updatedOn;
    private String confirmedBy;
    private Timestamp confirmedOn;

    // V2 fields
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String manufacturerCode;
    private String manufacturerName;
    private String middlewareId;
    private String middlewareHeaderId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private String purchaseOrderNumber;
    private String supplierName;
    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;

    private String c_text;
    private String plant_text;
    private String wh_text;
    private String status_text;
    private String source_branch_code;
    private String ref_doc_no;
    private Long ib_line_no;
    private String ref_doc_type;
    private String mfr_name;
    private String itm_code;
    private String text;
    private Double ord_qty;
    private Double accept_qty;
    private Double damage_qty;
    private Double var_qty;
    private Timestamp ctd_on;
    private Timestamp ib_cnf_on;

}
