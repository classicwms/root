package com.tekclover.wms.api.idmaster.model.shelfid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `C_ID`,`PLANT_ID`, `WH_ID`, `FL_ID`,`ST_SEC_ID`,`AISLE_ID`,`ROW_ID`,`SPAN_ID`,`SHELF_ID`,`LANG_ID`
 */
@Table(
		name = "tblshelfid",
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_shelfid",
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "FL_ID","ST_SEC_ID","AISLE_ID","ROW_ID","SPAN_ID","SHELF_ID","LANG_ID"})
				}
		)
@IdClass(ShelfIdCompositeKey.class)
public class ShelfId {
	
	
	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(50)") 
	private String companyCodeId;
		
	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)") 
	private String plantId;	
	
	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(50)") 
	private String warehouseId;
	
	@Id
	@Column(name = "FL_ID")
	private Long floorId;

	@Id
	@Column(name = "ST_SEC_ID", columnDefinition = "nvarchar(50)")
	private String storageSectionId;

	@Id
	@Column(name = "AISLE_ID", columnDefinition = "nvarchar(10)")
	private String aisleId;
	@Id
	@Column(name = "ROW_ID", columnDefinition = "nvarchar(10)")
	private String rowId;
	@Id
	@Column(name = "SPAN_ID", columnDefinition = "nvarchar(10)")
	private String spanId;

	@Id
	@Column(name = "SHELF_ID", columnDefinition = "nvarchar(10)")
	private String shelfId;

	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(5)") 
	private String languageId;
	
	@Column(name = "SHELF_TEXT", columnDefinition = "nvarchar(500)")
	private String shelfDescription;

	@Column(name="WAREHOUSE_ID_DESC",columnDefinition = "nvarchar(500)")
	private String warehouseIdAndDescription;

	@Column(name="PLANT_ID_DESC",columnDefinition = "nvarchar(500)")
	private String plantIdAndDescription;

	@Column(name="COMP_ID_DESC",columnDefinition = "nvarchar(500)")
	private String companyIdAndDescription;

	@Column(name="FLOOR_ID_DESC",columnDefinition = "nvarchar(500)")
	private String floorIdAndDescription;

	@Column(name="AISLED_ID_DESC",columnDefinition = "nvarchar(500)")
	private String aisledIdAndDescription;

	@Column(name="STORAGE_SEC_ID_DESC",columnDefinition = "nvarchar(500)")
	private String storageSectionIdAndDescription;

	@Column(name="ROW_ID_DESC",columnDefinition = "nvarchar(500)")
	private String rowIdAndDescription;

	@Column(name="SPAN_ID_DESC",columnDefinition = "nvarchar(500)")
	private String spanIdAndDescription;

	@Column(name = "IS_DELETED") 
	private Long deletionIndicator;

	@Column(name = "REF_FIELD_1",columnDefinition = "nvarchar(200)")
	private String referenceField1;

	@Column(name = "REF_FIELD_2",columnDefinition = "nvarchar(200)")
	private String referenceField2;

	@Column(name = "REF_FIELD_3",columnDefinition = "nvarchar(200)")
	private String referenceField3;

	@Column(name = "REF_FIELD_4",columnDefinition = "nvarchar(200)")
	private String referenceField4;

	@Column(name = "REF_FIELD_5",columnDefinition = "nvarchar(200)")
	private String referenceField5;

	@Column(name = "REF_FIELD_6",columnDefinition = "nvarchar(200)")
	private String referenceField6;

	@Column(name = "REF_FIELD_7",columnDefinition = "nvarchar(200)")
	private String referenceField7;

	@Column(name = "REF_FIELD_8",columnDefinition = "nvarchar(200)")
	private String referenceField8;

	@Column(name = "REF_FIELD_9",columnDefinition = "nvarchar(200)")
	private String referenceField9;

	@Column(name = "REF_FIELD_10",columnDefinition = "nvarchar(200)")
	private String referenceField10;
	
	@Column(name = "CTD_BY",columnDefinition = "nvarchar(50)")
	private String createdBy;

	@Column(name = "CTD_ON")
    private Date createdOn = new Date();

	@Column(name = "UTD_BY",columnDefinition = "nvarchar(50)")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();	
}
