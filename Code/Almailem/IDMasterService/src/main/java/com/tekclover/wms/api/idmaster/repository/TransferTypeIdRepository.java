package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.transfertypeid.TransferTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface TransferTypeIdRepository extends JpaRepository<TransferTypeId,Long>, JpaSpecificationExecutor<TransferTypeId> {
	
	public List<TransferTypeId> findAll();
	public Optional<TransferTypeId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndTransferTypeIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String transferTypeId, String languageId, Long deletionIndicator);
}