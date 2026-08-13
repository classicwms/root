package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.putawaystrategy.PutAwayStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional
public interface PutAwayStrategyRepository extends JpaRepository<PutAwayStrategy, String>, JpaSpecificationExecutor<PutAwayStrategy> {

    Optional<PutAwayStrategy> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndBrandAndArticleAndCategoryAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, String brand, String article, String category, Long deletionIndicator);

    @Query(value = "Select \n" +
            "COMP_ID_DESC As companyDesc, \n" +
            "PLANT_ID_DESC As plantDesc, \n" +
            "CONCAT (WH_ID, ' - ', WH_TEXT) As warehouseDesc \n" +
            "From \n" +
            "tblwarehouseid \n" +

            "Where \n" +
            "LANG_ID IN (:languageId) and \n" +
            "C_ID IN (:companyCodeId) and \n" +
            "PLANT_ID IN (:plantId) and \n" +
            "WH_ID IN (:warehouseId) and \n" +
            "is_deleted = 0", nativeQuery = true)
    IKeyValuePair getDescription(@Param(value = "languageId") String languageId,
                                 @Param(value = "companyCodeId") String companyCodeId,
                                 @Param(value = "plantId") String plantId,
                                 @Param(value = "warehouseId") String warehouseId);
}
