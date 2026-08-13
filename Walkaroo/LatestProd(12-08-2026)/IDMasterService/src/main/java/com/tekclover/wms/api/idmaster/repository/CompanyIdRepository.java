package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.idmaster.model.IKeyValuePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.companyid.CompanyId;


@Repository
@Transactional
public interface CompanyIdRepository extends JpaRepository<CompanyId,Long>, JpaSpecificationExecutor<CompanyId> {
	
	public List<CompanyId> findAll();
	public Optional<CompanyId> 
		findByCompanyCodeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String languageId, Long deletionIndicator);

	@Query(value ="select  tl.c_id AS companyCodeId,tl.c_text AS description\n"+
			" from tblcompanyid tl \n" +
			"WHERE \n"+
			"tl.c_id IN (:companyCodeId) and tl.lang_id IN (:languageId) and \n"+
			"tl.is_deleted=0 ",nativeQuery = true)

	public IKeyValuePair getCompanyIdAndDescription(@Param(value="companyCodeId") String companyCodeId,
													@Param(value="languageId") String languageId);

	//Description
	@Query(value = "select CONCAT(tc.c_id,'-',tc.c_text) companyDesc,\n" +
			"CONCAT(tp.plant_id,'-',tp.plant_text) plantDesc,\n" +
			"CONCAT(tw.wh_id,'-',tw.wh_text) warehouseDesc from \n" +
			"tblcompanyid tc\n" +
			"join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id\n" +
			"join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id=tc.lang_id and tw.plant_id = tp.plant_id\n" +
			"where\n" +
			"tc.c_id IN (:companyCodeId) and \n" +
			"tc.lang_id IN (:languageId) and \n" +
			"tp.plant_id IN(:plantId) and \n" +
			"tw.wh_id IN (:warehouseId) and \n" +
			"tc.is_deleted=0 and \n" +
			"tp.is_deleted=0 and \n" +
			"tw.is_deleted=0", nativeQuery = true)
	public IKeyValuePair getDescription(@Param(value = "companyCodeId") String companyCodeId,
										@Param(value = "plantId") String plantId,
										@Param(value = "languageId") String languageId,
										@Param(value = "warehouseId") String warehouseId);

}