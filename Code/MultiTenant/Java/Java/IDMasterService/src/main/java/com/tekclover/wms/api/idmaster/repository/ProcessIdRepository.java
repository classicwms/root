package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.processid.ProcessId;
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
public interface ProcessIdRepository extends JpaRepository<ProcessId,Long>, JpaSpecificationExecutor<ProcessId> {
	
	public List<ProcessId> findAll();
	public Optional<ProcessId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndProcessIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String processId, String languageId, Long deletionIndicator);

	@Query(value ="select  tl.process_id AS processId,tl.process_text AS description\n"+
			" from tblprocessid tl \n" +
			"WHERE \n"+
			"tl.process_id IN (:processId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and  \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getProcessIdAndDescription(@Param(value="processId") String processId,
													@Param(value = "languageId")String languageId,
													@Param(value = "companyCodeId")String companyCodeId,
													@Param(value = "plantId")String plantId,
													@Param(value = "warehouseId")String warehouseId);

}