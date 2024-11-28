package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.bom.BomLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface BomLineRepository extends JpaRepository<BomLine, Long>, JpaSpecificationExecutor<BomLine> {

    public List<BomLine> findAll();

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param bomNumber
     * @return
     */
    public List<BomLine>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumber(String languageId, String companyCode,
                                                                       String plantId, String warehouseId, Long bomNumber);

    public List<BomLine>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumberAndDeletionIndicator
            (String languageId, String companyCode, String plantId, String warehouseId, Long bomNumber, Long deletionIndicator);

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param bomNumber
     * @param childItemCode
     * @param deletionIndicator
     * @return
     */
    public Optional<BomLine>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndBomNumberAndChildItemCodeAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId,
            Long bomNumber, String childItemCode, Long deletionIndicator);
}