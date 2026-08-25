package com.tekclover.wms.api.enterprise.transaction.service;

import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.repository.PreInboundHeaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class CountBarService {
	
	@Autowired
	private PreInboundHeaderRepository preInboundHeaderRepository;
	
	/**
	 * 
	 * @param warehouseId
	 * @return
	 */
	public Long getPreInboundCount (String warehouseId) {
		List<Long> statusIds = Arrays.asList(new Long[] {6L, 7L});
		long preInboundCount = preInboundHeaderRepository.countByWarehouseIdAndStatusIdIn(warehouseId, statusIds);
		if (preInboundCount == 0) {
			throw new BadRequestException("The given PreInboundHeader ID : " + warehouseId + " doesn't exist.");
		} 
		return preInboundCount;
	}
}