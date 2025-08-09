package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import com.tekclover.wms.api.idmaster.model.spanid.SpanId;
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
public interface SpanIdRepository extends JpaRepository<SpanId,Long>, JpaSpecificationExecutor<SpanId> {
	
	public List<SpanId> findAll();
	public Optional<SpanId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndAisleIdAndSpanIdAndFloorIdAndStorageSectionIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId,String aisleId, String spanId,Long floorId,String storageSectionId, String languageId, Long deletionIndicator);

	@Query(value ="select  tl.span_id AS spanId,tl.span_text AS description\n"+
			" from tblspanid tl \n" +
			"WHERE \n"+
			"tl.span_id IN (:spanId)and tl.lang_id IN (:languageId) and tl.wh_id IN (:warehouseId) and tl.st_sec_id IN (:storageSectionId) and tl.aisle_id IN (:aisleId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.row_id IN (:rowId) and tl.fl_id IN (:floorId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)//AISLE_ID

	public IKeyValuePair getSpanIdAndDescription(@Param(value="spanId") String spanId,
												 @Param(value = "languageId")String languageId,
												 @Param(value = "warehouseId")String warehouseId,
												 @Param(value = "storageSectionId")String storageSectionId,
												 @Param(value = "aisleId")String aisleId,
												 @Param(value = "companyCodeId")String companyCodeId,
												 @Param(value = "plantId")String plantId,
												 @Param(value = "floorId")String floorId,
												 @Param(value = "rowId")String rowId);
}