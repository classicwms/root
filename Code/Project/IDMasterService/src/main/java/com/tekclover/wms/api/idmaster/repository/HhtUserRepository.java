package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.hhtuser.HhtUser;

@Repository
@Transactional
public interface HhtUserRepository extends JpaRepository<HhtUser,Long>, JpaSpecificationExecutor<HhtUser> {
	
	public List<HhtUser> findAll();
	
	public Optional<HhtUser> 
		findByWarehouseIdAndUserIdAndDeletionIndicator(
				String warehouseId, String userId, Long deletionIndicator);

	public HhtUser findByUserIdAndWarehouseIdAndDeletionIndicator(String userId, String warehouseId, long l);

	public List<HhtUser> findByWarehouseIdAndDeletionIndicator(String warehouseId, Long deletionIndicator);
}