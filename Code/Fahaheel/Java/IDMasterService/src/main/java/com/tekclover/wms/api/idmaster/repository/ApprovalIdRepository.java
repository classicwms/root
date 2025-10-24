package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.approvalid.ApprovalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface ApprovalIdRepository extends JpaRepository<ApprovalId,Long>, JpaSpecificationExecutor<ApprovalId> {
	
	public List<ApprovalId> findAll();
	public Optional<ApprovalId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndApprovalIdAndApprovalLevelAndApprovalProcessIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String approvalId,String approvalLevel,String approvalProcessId,String languageId, Long deletionIndicator);
}