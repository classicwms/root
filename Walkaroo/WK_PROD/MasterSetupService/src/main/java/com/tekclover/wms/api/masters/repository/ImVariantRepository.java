package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.imvariant.ImVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImVariantRepository extends JpaRepository<ImVariant,String>, JpaSpecificationExecutor<ImVariant> {


    List<ImVariant> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndVariantCodeAndVariantTypeAndAndVariantSubCodeAndDeletionIndicator(
            String companyCodeId,String languageId,String plantId,String warehouseId,String itemCode,String variantCode,String variantType,String variantSubCode,Long deletionIndicator);


    List<ImVariant> findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(String companyCodeId, String languageId, String plantId, String warehouseId, String itemCode, Long deletionIndicator);
}
