package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.bom.BomHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface BomHeaderRepository extends JpaRepository<BomHeader, Long>, JpaSpecificationExecutor<BomHeader> {

    public List<BomHeader> findAll();

    public Optional<BomHeader>
    findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndParentItemCodeAndDeletionIndicator(
            String languageId,
            String companyCode,
            String plantId,
            String warehouseId,
            String parentItemCode,
            Long deletionIndicator);

}