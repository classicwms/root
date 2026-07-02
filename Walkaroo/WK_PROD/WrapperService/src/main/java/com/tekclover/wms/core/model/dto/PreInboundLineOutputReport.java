package com.tekclover.wms.core.model.dto;

import com.tekclover.wms.core.model.transaction.InventoryDetail;
import com.tekclover.wms.core.model.transaction.InventoryDetailsV2;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PreInboundLineOutputReport {

    private String languageId;
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String preInboundNo;
    private String refDocNumber;
    private Long lineNo;
    private String itemCode;
    private Long inboundOrderTypeId;
    private Long variantCode;
    private String variantSubCode;
    private Long statusId;
    private String itemDescription;
    private String containerNo;
    private String invoiceNo;
    private String businessPartnerCode;
    private String partnerItemNo;
    private String brandName;
    private String manufacturerPartNo;
    private String hsnCode;
    private Date expectedArrivalDate;
    private Double orderQty;
    private String orderUom;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String numberOfPallets;
    private String numberOfCases;
    private Double itemPerPalletQty;
    private Double itemCaseQty; // PACK_QTY in AX_API
    private String createdBy;
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;
    private String manufacturerCode;
    private String manufacturerName;
    private String storageSectionId;
    private String origin;
    private String supplierName;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String middlewareId;
    private String middlewareHeaderId;
    private String middlewareTable;
    private String referenceDocumentType;
    private String purchaseOrderNumber;
    private String manufacturerFullName;
    private String branchCode;
    private String transferOrderNo;
    private String isCompleted;

    private List<InventoryDetailsV2> inventoryDetail;

    /*----------------Walkaroo changes------------------------------------------------------*/
    private String materialNo;
    private String priceSegment;
    private String articleNo;
    private String gender;
    private String color;
    private String size;
    private String noPairs;
    private String barcodeId;
    private String maxCapacity;
    private String superMaxCapacity;
    private String storageBin;
}
