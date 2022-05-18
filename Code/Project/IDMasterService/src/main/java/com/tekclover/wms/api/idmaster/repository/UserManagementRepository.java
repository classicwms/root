package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.user.UserManagement;

@Repository
public interface UserManagementRepository extends JpaRepository<UserManagement, Long> {

	public Optional<UserManagement> findByUserId(String userId);
	
	public List<UserManagement> findByUserIdAndDeletionIndicator(String userId, Long deletionIndicator);

	public UserManagement findByWarehouseIdAndUserIdAndDeletionIndicator(String warehouseId, String userId,
			Long deletionIndicator);
}