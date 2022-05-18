package com.tekclover.wms.api.idmaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.idmaster.model.binclassid.BinClassId;


@Repository
@Transactional
public interface BinClassIdRepository extends JpaRepository<BinClassId,Long>, JpaSpecificationExecutor<BinClassId> {

	public List<BinClassId> findAll();
	
	/**
	 * 
	 * @param companyCode
	 * @param plantId
	 * @param warehouseId
	 * @param binClassId
	 * @param languageId
	 * @param deletionIndicator
	 * @return
	 */
	public Optional<BinClassId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndBinClassIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, 
				String plantId, 
				String warehouseId, 
				Long binClassId,
				String languageId,
				Long deletionIndicator);

	public Optional<BinClassId> findByWarehouseIdAndDeletionIndicator(String warehouseId, long l);
	
}
