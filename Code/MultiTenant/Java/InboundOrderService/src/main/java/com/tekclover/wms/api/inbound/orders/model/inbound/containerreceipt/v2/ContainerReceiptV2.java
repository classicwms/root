package com.tekclover.wms.api.inbound.orders.model.inbound.containerreceipt.v2;

import com.tekclover.wms.api.inbound.orders.model.inbound.containerreceipt.ContainerReceipt;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class ContainerReceiptV2 extends ContainerReceipt {

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;

	@Column(name = "STATUS_TEXT", columnDefinition = "nvarchar(150)")
	private String statusDescription;

	@Column(name = "PURCHASE_ORDER_NUMBER", columnDefinition = "nvarchar(150)")
	private String purchaseOrderNumber;

	@Column(name = "MIDDLEWARE_ID", columnDefinition = "nvarchar(50)")
	private String middlewareId;

	@Column(name = "MIDDLEWARE_TABLE", columnDefinition = "nvarchar(50)")
	private String middlewareTable;

    @Column(name = "REF_FIELD_11")
    private Date referenceField11;

    @Column(name = "REF_FIELD_12")
    private Date referenceField12;

    @Column(name = "REF_FIELD_13")
    private Date referenceField13;

    @Column(name = "REF_FIELD_14")
    private Date referenceField14;

    @Column(name = "REF_FIELD_15")
    private Date referenceField15;

    @Column(name = "REF_FIELD_16")
    private Date referenceField16;

    @Column(name = "REF_FIELD_17")
    private Date referenceField17;

    @Column(name = "REF_FIELD_18")
    private Date referenceField18;

    @Column(name = "REF_FIELD_19")
    private Date referenceField19;

    @Column(name = "REF_FIELD_20")
    private Date referenceField20;

    @Column(name = "REF_FIELD_21")
    private String referenceField21;

    @Column(name = "REF_FIELD_22")
    private String referenceField22;

    @Column(name = "REF_FIELD_23")
    private String referenceField23;

    @Column(name = "REF_FIELD_24")
    private String referenceField24;

    @Column(name = "REF_FIELD_25")
    private String referenceField25;

    @Column(name = "REF_FIELD_26")
    private String referenceField26;

    @Column(name = "REF_FIELD_27")
    private String referenceField27;

    @Column(name = "REF_FIELD_28")
    private String referenceField28;

    @Column(name = "REF_FIELD_29")
    private String referenceField29;

    @Column(name = "REF_FIELD_30")
    private String referenceField30;

}
