package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.approvalprocessid.ApprovalProcessId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
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

	@Query(value ="select  tl.app_process_id AS approvalProcessId,tl.app_process AS description\n"+
			" from tblapprovalprocessid tl \n" +
			"WHERE \n"+
			"tl.app_process_id IN (:approvalProcessId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getApprovalProcessIdAndDescription(@Param(value="approvalProcessId") String approvalProcessId,
															@Param(value = "languageId") String languageId,
															@Param(value = "companyCodeId")String companyCodeId,
															@Param(value = "plantId")String plantId,
															@Param(value = "warehouseId")String warehouseId);

}