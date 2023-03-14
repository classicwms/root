package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.threepl.pricelist.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface PriceListRepository extends JpaRepository<PriceList,Long>, JpaSpecificationExecutor<PriceList> {
    Optional<PriceList> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleAndPriceListIdAndServiceTypeIdAndLanguageIdAndDeletionIndicator(String companyCode, String plantId, String warehouseId, Long module, Long priceListId, Long serviceTypeId, String languageId, Long deletionIndicator);
}
