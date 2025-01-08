package com.tekclover.wms.api.masters.repository;


import com.tekclover.wms.api.masters.model.numberrangeitem.NumberRangeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
@Transactional
public interface NumberRangeItemRepository extends JpaRepository<NumberRangeItem, Long>, JpaSpecificationExecutor<NumberRangeItem> {

    Optional<NumberRangeItem> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemTypeIdAndSequenceNoAndDeletionIndicator(String companyCodeId, String plantId, String languageId, String warehouseId, Long itemTypeId, Long sequenceNo, Long deletionIndicator);
}