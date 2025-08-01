package com.tekclover.wms.api.inbound.orders.model.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_CODE`, `UOM_ID`,'MFR_PART'
 */
@Table(
		name = "tblimbasicdata1",
		uniqueConstraints = {
				@UniqueConstraint (
						name = "unique_key_imbasicdata1",
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ITM_CODE", "UOM_ID","MFR_PART"})
		}
)
@IdClass(ImBasicData1CompositeKey.class)
public class ImBasicData1 {

	@Id
	@Column(name = "UOM_ID")
	private String uomId;

	@Id
	@Column(name = "LANG_ID")
	private String languageId;

	@Id
	@Column(name = "C_ID")
	private String companyCodeId;

	@Id
	@Column(name = "PLANT_ID")
	private String plantId;

	@Id
	@Column(name = "WH_ID")
	private String warehouseId;

	@Id
	@Column(name = "ITM_CODE")
	private String itemCode;

	@Id
	@Column(name = "MFR_PART")
	private String manufacturerPartNo;

	@Column(name = "TEXT")
	private String description;

	@Column(name = "MODEL")
	private String model;

	@Column(name = "SPEC_01")
	private String specifications1;

	@Column(name = "SPEC_02")
	private String specifications2;

	@Column(name = "EAN_UPC_NO")
	private String eanUpcNo;


	@Column(name = "HSN_CODE")
	private String hsnCode;

	@Column(name = "ITM_TYP_ID")
	private Long itemType;

	@Column(name = "ITM_GRP_ID")
	private Long itemGroup;

	@Column(name = "SUB_ITM_GRP_ID")
	private Long subItemGroup;

	@Column(name = "ST_SEC_ID")
	private String storageSectionId;

	@Column(name = "TOT_STK")
	private Double totalStock;

	@Column(name = "MIN_STK")
	private Double minimumStock;

	@Column(name = "MAX_STK")
	private Double maximumStock;

	@Column(name = "RE_ORD_LVL")
	private Double reorderLevel;

	@Column(name="CAP_CHK")
	private Boolean capacityCheck;

	@Column(name = "REP_QTY")
	private Double replenishmentQty;

	@Column(name = "SAFTY_STCK")
	private Double safetyStock;

	@Column(name = "CAP_UNIT")
	private String capacityUnit;

	@Column(name = "CAP_UOM")
	private String capacityUom;

	@Column(name = "QUANTITY")
	private String quantity;

	@Column(name = "WEIGHT")
	private Double weight;

	@Column(name = "STATUS_ID")
	private Long statusId;

	@Column(name="SHELF_LIFE_IND")
	private Boolean shelfLifeIndicator;

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

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
	private Date createdOn = new Date();

	@Column(name = "UTD_BY")
	private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();

	//fields from ImBasicData2
	@Column(name = "LENGTH")
	private Double length;

	@Column(name = "WIDTH")
	private Double width;

	@Column(name = "HEIGHT")
	private Double height;

	@Column(name = "DIM_UOM")
	private String dimensionUom;

	@Column(name = "VOLUME")
	private Double volume;
}