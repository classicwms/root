package com.tekclover.wms.api.inbound.transaction.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@ToString(callSuper = true)
public class StorageBinV2 extends StorageBin {

	@Column(name = "CAP_CHECK")
	private boolean capacityCheck;

	@Column(name="ALLOC_VOL")
	private Double allocatedVolume;

}
