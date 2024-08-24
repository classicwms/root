//package com.tekclover.wms.api.transaction.service;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
//import com.tekclover.wms.api.transaction.repository.PreInboundHeaderRepository;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class CountBarService {
//
//	@Autowired
//	private PreInboundHeaderRepository preInboundHeaderRepository;
//
//	/**
//	 *
//	 * @param warehouseId
//	 * @return
//	 */
//	public Long getPreInboundCount (String warehouseId) {
//		List<Long> statusIds = Arrays.asList(new Long[] {6L, 7L});
//		long preInboundCount = preInboundHeaderRepository.countByWarehouseIdAndStatusIdIn(warehouseId, statusIds);
//		if (preInboundCount == 0) {
//			throw new BadRequestException("The given PreInboundHeader ID : " + warehouseId + " doesn't exist.");
//		}
//		return preInboundCount;
//	}
//}