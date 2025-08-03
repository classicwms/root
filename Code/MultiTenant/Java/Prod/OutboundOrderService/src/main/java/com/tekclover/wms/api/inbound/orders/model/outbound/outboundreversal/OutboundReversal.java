package com.tekclover.wms.api.inbound.orders.model.outbound.outboundreversal;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `REF_DOC_NO`, `PLANT_ID`, `WH_ID`, `OB_REVERSAL_NO`, `REVERSAL_TYPE`
 */
@Table(
		name = "tbloutboundreversal", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_outboundreversal", 
						columnNames = {"LANG_ID", "REF_DOC_NO", "PLANT_ID", "WH_ID", "OB_REVERSAL_NO", "REVERSAL_TYPE", "PARTNER_ITEM_BARCODE"})
				}
		)
@IdClass(OutboundReversalCompositeKey.class)
public class OutboundReversal { 
	
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)") 
	private String languageId;
	
	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
	private String companyCodeId;
	
	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
	private String plantId;
	
	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
	private String warehouseId;
	
	@Id
	@Column(name = "OB_REVERSAL_NO") 
	private String outboundReversalNo;
	
	@Id
	@Column(name = "REVERSAL_TYPE")
	private String reversalType;

	@Id
	@Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(100)")
	private String barcodeId;	
	
	@Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)")
	private String refDocNumber;
	
	@Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(100)")
	private String partnerCode;
	
	@Column(name = "ITM_CODE", columnDefinition = "nvarchar(255)")
	private String itemCode;
	
	@Column(name = "PACK_BARCODE", columnDefinition = "nvarchar(50)")
	private String packBarcode;
	
	@Column(name = "REV_QTY") 
	private Double reversedQty;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
	@Column(name = "REF_FIELD_1") 
	private String referenceField1;
	
	@Column(name = "REF_FIELD_2") 
	private String referenceField2;
	
	@Column(name = "REF_FIELD_3")
	private String referenceField3;
	
	@Column(name = "REF_FIELD_4")
	private String referenceField4;
	
	@Column(name = "REF_FIELD_5")
	private String referenceField5;
	
	@Column(name = "REF_FIELD_6") 
	private String referenceField6;
	
	@Column(name = "REF_FIELD_7")
	private String referenceField7;
	
	@Column(name = "REF_FIELD_8")
	private String referenceField8;
	
	@Column(name = "REF_FIELD_9")
	private String referenceField9;
	
	@Column(name = "REF_FIELD_10")
	private String referenceField10;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator;
	
	@Column(name = "OB_REV_BY")
	private String reversedBy;
	
	@Column(name = "OB_REV_ON") 
	private Date reversedOn = new Date();
}