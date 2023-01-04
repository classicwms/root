package com.ustorage.api.trans.service;

import com.ustorage.api.trans.model.workorder.WoProcessedBy;
import com.ustorage.api.trans.repository.WoProcessedByRepository;
import com.ustorage.api.trans.repository.WorkOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WoProcessedByService {

	@Autowired
	private WorkOrderRepository workOrderRepository;

	@Autowired
	private WoProcessedByRepository woProcessedByRepository;


	public List<WoProcessedBy> getWoProcessedBy () {
		List<WoProcessedBy> woProcessedByList =  woProcessedByRepository.findAll();
		woProcessedByList = woProcessedByList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return woProcessedByList;
	}

	/**
	 * getWoProcessedBy
	 * @param workOrderId
	 * @return
	 */
	public List<WoProcessedBy> getWoProcessedBy (String workOrderId) {
		List<WoProcessedBy> woProcessedBy = woProcessedByRepository.findByWorkOrderIdAndDeletionIndicator(workOrderId, 0L);
		if (woProcessedBy.isEmpty()) {
			return null;
		}
		woProcessedBy = woProcessedBy.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return woProcessedBy;
	}

	
	/**
	 * deleteWoProcessedBy
	 */
	public void deleteWoProcessedBy (String workOrderId, String loginUserID) {
		List<WoProcessedBy> woprocessedby = getWoProcessedBy(workOrderId);
		if (woprocessedby != null) {
			for(WoProcessedBy newWoProcessedBy: woprocessedby){
				if(newWoProcessedBy.getDeletionIndicator()==0){
					newWoProcessedBy.setDeletionIndicator(1L);
					newWoProcessedBy.setUpdatedBy(loginUserID);
					newWoProcessedBy.setUpdatedOn(new Date());
					woProcessedByRepository.save(newWoProcessedBy);
				}
			}
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + workOrderId);
		}
	}
	

}
