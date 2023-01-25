package com.ustorage.api.trans.service;

import com.ustorage.api.trans.model.paymentvoucher.ReferenceField3;
import com.ustorage.api.trans.repository.ReferenceField3Repository;
import com.ustorage.api.trans.repository.PaymentVoucherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReferenceField3Service {

	@Autowired
	private PaymentVoucherRepository paymentVoucherRepository;

	@Autowired
	private ReferenceField3Repository referenceField3Repository;


	public List<ReferenceField3> getReferenceField3 () {
		List<ReferenceField3> referenceField3List =  referenceField3Repository.findAll();
		referenceField3List = referenceField3List.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return referenceField3List;
	}

	/**
	 * getReferenceField3
	 * @param paymentVoucherId
	 * @return
	 */
	public List<ReferenceField3> getReferenceField3 (String paymentVoucherId) {
		List<ReferenceField3> referenceField3 = referenceField3Repository.findByVoucherIdAndDeletionIndicator(paymentVoucherId, 0L);
		if (referenceField3.isEmpty()) {
			return null;
		}
		referenceField3 = referenceField3.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return referenceField3;
	}

	
	/**
	 * deleteReferenceField3
	 */
	public void deleteReferenceField3 (String paymentVoucherId, String loginUserID) {
		List<ReferenceField3> referencefield3 = getReferenceField3(paymentVoucherId);
		if (referencefield3 != null) {
			for(ReferenceField3 newReferenceField3: referencefield3){
				if(newReferenceField3.getDeletionIndicator()==0){
					newReferenceField3.setDeletionIndicator(1L);
					newReferenceField3.setUpdatedBy(loginUserID);
					newReferenceField3.setUpdatedOn(new Date());
					referenceField3Repository.save(newReferenceField3);
				}
			}
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + paymentVoucherId);
		}
	}
	

}
