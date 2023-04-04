package com.tekclover.wms.api.idmaster.model.itemgroupid;

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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_TYPE_ID`, `ITM_GRP_ID`, `IMT_GRP`
 */
@Table(
		name = "tblitemgroupid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_itemgroupid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "ITM_TYPE_ID", "ITM_GRP_ID", "IMT_GRP"})
				}
		)
@IdClass(ItemGroupIdCompositeKey.class)
public class ItemGroupId { 
	
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
	@Column(name = "ITM_TYPE_ID")
	private Long itemTypeId;
	
	@Id
	@Column(name = "ITM_GRP_ID")
	private Long itemGroupId;
	
	@Id
	@Column(name = "IMT_GRP") 
	private String itemGroup;
	
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
}
