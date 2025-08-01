package com.tekclover.wms.api.outbound.transaction.model.outbound.v2;

import com.tekclover.wms.api.outbound.transaction.model.outbound.OutboundLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class OutboundLineV2 extends OutboundLine {

    @Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
    private String companyDescription;

    @Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
    private String plantDescription;

    @Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
    private String warehouseDescription;

    @Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
    private String statusDescription;

    @Column(name = "MFR_NAME", columnDefinition = "nvarchar(255)")
    private String manufacturerName;

    @Column(name = "ST_SEC_ID")
    private String storageSectionId;

    @Column(name = "MIDDLEWARE_ID")
    private Long middlewareId;

    @Column(name = "MIDDLEWARE_HEADER_ID")
    private Long middlewareHeaderId;

    @Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
    private String middlewareTable;

    @Column(name = "REF_DOC_TYPE", columnDefinition = "nvarchar(150)")
    private String referenceDocumentType;

    @Column(name = "SUPPLIER_INVOICE_NO", columnDefinition = "nvarchar(150)")
    private String supplierInvoiceNo;

    @Column(name = "SALES_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
    private String salesOrderNumber;

    @Column(name = "MANUFACTURER_FULL_NAME", columnDefinition = "nvarchar(150)")
    private String manufacturerFullName;

    @Column(name = "SALES_INVOICE_NUMBER", columnDefinition = "nvarchar(150)")
    private String salesInvoiceNumber;

    @Column(name = "PICK_LIST_NUMBER", columnDefinition = "nvarchar(150)")
    private String pickListNumber;

    @Column(name = "TOKEN_NUMBER", columnDefinition = "nvarchar(150)")
    private String tokenNumber;

    @Column(name = "INVOICE_DATE")
    private Date invoiceDate;

    @Column(name = "DELIVERY_TYPE", columnDefinition = "nvarchar(100)")
    private String deliveryType;

    @Column(name = "CUSTOMER_ID", columnDefinition = "nvarchar(150)")
    private String customerId;

    @Column(name = "CUSTOMER_NAME", columnDefinition = "nvarchar(150)")
    private String customerName;

    @Column(name = "ADDRESS", columnDefinition = "nvarchar(500)")
    private String address;

    @Column(name = "PHONE_NUMBER", columnDefinition = "nvarchar(100)")
    private String phoneNumber;

    @Column(name = "ALTERNATE_NO", columnDefinition = "nvarchar(100)")
    private String alternateNo;

    @Column(name = "STATUS", columnDefinition = "nvarchar(100)")
    private String status;

    /*---------------------------------------------------------------------------------------------------------*/

    @Column(name = "TRANSFER_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String transferOrderNo;

    @Column(name = "RET_ORDER_NO", columnDefinition = "nvarchar(50)")
    private String returnOrderNo;

    @Column(name = "IS_COMPLETED", columnDefinition = "nvarchar(20)")
    private String isCompleted;

    @Column(name = "IS_CANCELLED", columnDefinition = "nvarchar(20)")
    private String isCancelled;

    @Column(name = "TARGET_BRANCH_CODE", columnDefinition = "nvarchar(50)")
    private String targetBranchCode;

    @Column(name = "CUSTOMER_TYPE", columnDefinition = "nvarchar(255)")
    private String customerType;

    @Column(name = "HE_NO", columnDefinition = "nvarchar(255)")
    private String handlingEquipment;

    @Column(name = "ASS_PICKER_ID")
    private String assignedPickerId;

    /*----------------Walkaroo changes------------------------------------------------------*/
    @Column(name = "MATERIAL_NO", columnDefinition = "nvarchar(50)")
    private String materialNo;

    @Column(name = "PRICE_SEGMENT", columnDefinition = "nvarchar(50)")
    private String priceSegment;

    @Column(name = "ARTICLE_NO", columnDefinition = "nvarchar(50)")
    private String articleNo;

    @Column(name = "GENDER", columnDefinition = "nvarchar(50)")
    private String gender;

    @Column(name = "COLOR", columnDefinition = "nvarchar(50)")
    private String color;

    @Column(name = "SIZE", columnDefinition = "nvarchar(50)")
    private String size;

    @Column(name = "NO_PAIRS", columnDefinition = "nvarchar(50)")
    private String noPairs;

    @Transient
    private String tracking;
    /*----------------------Impex--------------------------------------------------*/
    @Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
    private String alternateUom;

    @Column(name = "NO_BAGS")
    private Double noBags;

    @Column(name = "BAG_SIZE")
    private Double bagSize;

    @Column(name = "MRP")
    private Double mrp;

    @Column(name = "ITM_TYP", columnDefinition = "nvarchar(100)")
    private String itemType;

    @Column(name = "ITM_GRP", columnDefinition = "nvarchar(100)")
    private String itemGroup;

    @Column(name = "BRAND", columnDefinition = "nvarchar(100)")
    private String brand;

    @Column(name = "IMS_SALE_TYP_CODE")
    private Integer imsSaleTypeCode;

    /*----------------------REEFERON--------------------------------------------------*/
    @Column(name = "QTY_IN_CASE")
    private Double qtyInCase;

    @Column(name = "QTY_IN_PIECE")
    private Double qtyInPiece;

    @Column(name = "QTY_IN_CRATE")
    private Double qtyInCrate;

    @Column(name = "DELIVERED_PERCENTAGE", columnDefinition = "nvarchar(255)")
    private String deliveredPercentage;

}

