package com.tekclover.wms.api.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutawayHeaderInt;

@Repository
@Transactional
public interface PutAwayHeaderIntRepository extends JpaRepository<PutawayHeaderInt, Long> {

	public List<PutawayHeaderInt> findByReRun (Long l);
	
}