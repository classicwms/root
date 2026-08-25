package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.rowid.RowId;


@Repository
@Transactional
public interface RowIdRepository extends JpaRepository<RowId,Long>, JpaSpecificationExecutor<RowId> {
	
	public List<RowId> findAll();
	public Optional<RowId>
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndFloorIdAndStorageSectionIdAndRowIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, Long floorId, String storageSectionId, String rowId, String languageId, Long deletionIndicator);

	@Query(value ="select  tl.row_id AS rowId,tl.row_text AS description\n"+
			" from tblrowid tl \n" +
			"WHERE \n"+
			"tl.row_id IN (:rowId)and tl.lang_id IN (:languageId) and tl.wh_id IN (:warehouseId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.st_sec_id IN (:storageSectionId) and tl.fl_id IN (:floorId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getRowIdAndDescription(@Param(value="rowId") String rowId,
												@Param(value = "languageId")String languageId,
												@Param(value = "warehouseId")String warehouseId,
												@Param(value = "companyCodeId")String companyCodeId,
												@Param(value = "plantId")String plantId,
												@Param(value = "storageSectionId")String storageSectionId,
												@Param(value = "floorId")String floorId);
}