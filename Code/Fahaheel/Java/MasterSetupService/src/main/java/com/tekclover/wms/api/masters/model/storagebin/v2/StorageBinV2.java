package com.tekclover.wms.api.masters.model.storagebin.v2;

import com.tekclover.wms.api.masters.model.storagebin.StorageBin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table
@ToString(callSuper = true)
public class StorageBinV2 extends StorageBin {

	@Column(name = "CAP_CHECK")
	private boolean capacityCheck;

	@Column(name="ALLOC_VOL")
	private Double allocatedVolume;

	@Column(name = "CAP_UNIT")
	private String capacityUnit;

	@Column(name = "LENGTH")
	private Double length;

	@Column(name = "WIDTH")
	private Double width;

	@Column(name = "HEIGHT")
	private Double height;

	@Column(name = "CAP_UOM")
	private String capacityUom;

	@Column(name = "QUANTITY")
	private String quantity;

	@Column(name = "WEIGHT")
	private Double weight;

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;

}
