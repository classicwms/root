package com.tekclover.wms.api.inbound.transaction.repository;

import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutawayHeaderInt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PutAwayHeaderIntRepository extends JpaRepository<PutawayHeaderInt, Long> {

	public List<PutawayHeaderInt> findByReRun (Long l);
	
}