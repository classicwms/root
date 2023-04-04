package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.approvalprocessid.ApprovalProcessId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface ApprovalProcessIdRepository extends JpaRepository<ApprovalProcessId,Long>, JpaSpecificationExecutor<ApprovalProcessId> {
	
	public List<ApprovalProcessId> findAll();
	public Optional<ApprovalProcessId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndApprovalProcessIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String approvalProcessId, String languageId, Long deletionIndicator);
}