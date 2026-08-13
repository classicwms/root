package com.tekclover.wms.api.masters.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.imalternateuom.ImAlternateUom;

@Repository
@Transactional
public interface ImAlternateUomRepository extends JpaRepository<ImAlternateUom,Long>, JpaSpecificationExecutor<ImAlternateUom> {

    Optional<ImAlternateUom> findByAlternateUom(String alternateUom);

    List<ImAlternateUom> findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndDeletionIndicator(
            String alternateUom, String companyCodeId, String plantId,
            String warehouseId, String itemCode, String uomId,
            String languageId, Long deletionIndicator);

    @Query(value ="select max(ID)+1 \n"+
            " from tblimalternateuom ",nativeQuery = true)
    public Long getId();

    ImAlternateUom findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndLanguageIdAndIdAndDeletionIndicator(
            String alternateUom, String companyCodeId, String plantId,
            String warehouseId, String itemCode, String uomId,
            String languageId, Long id, Long deletionIndicator);

    List<ImAlternateUom> findByAlternateUomAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndLanguageIdAndDeletionIndicator(
            String alternateUom, String companyCodeId, String plantId,
            String warehouseId, String itemCode, String languageId, Long deletionIndicator);
}