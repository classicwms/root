package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.dateformatid.DateFormatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface DateFormatIdRepository extends JpaRepository<DateFormatId,Long>, JpaSpecificationExecutor<DateFormatId> {
	
	public List<DateFormatId> findAll();
	public Optional<DateFormatId> 
		findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDateFormatIdAndLanguageIdAndDeletionIndicator(
				String companyCodeId, String plantId, String warehouseId, String dateFormatId, String languageId, Long deletionIndicator);
}