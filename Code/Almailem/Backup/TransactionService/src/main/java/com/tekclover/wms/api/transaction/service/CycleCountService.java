package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.model.warehouse.cyclecount.CycleCountHeader;
import com.tekclover.wms.api.transaction.repository.CycleCountHeaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Service
public class CycleCountService {
	
	@Autowired
	private CycleCountHeaderRepository cycleCountHeaderRepository;

	/**
	 *
	 * @param cycleCountNo
	 * @param processStatusId
	 * @throws ParseException
	 */
	public void updateProcessedOrderV2(String cycleCountNo, Long processStatusId) throws ParseException {
		CycleCountHeader dbCycleCountHeader = cycleCountHeaderRepository.findByCycleCountNo(cycleCountNo);
		log.info("cycleCountNo : " + cycleCountNo);
		log.info("dbCycleCountHeader : " + dbCycleCountHeader);
		if (dbCycleCountHeader != null) {
			dbCycleCountHeader.setProcessedStatusId(processStatusId);
			dbCycleCountHeader.setOrderProcessedOn(new Date());
			cycleCountHeaderRepository.save(dbCycleCountHeader);
		}
	}

}
