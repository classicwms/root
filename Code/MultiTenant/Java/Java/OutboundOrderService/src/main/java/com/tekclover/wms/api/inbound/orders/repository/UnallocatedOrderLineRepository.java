package com.tekclover.wms.api.inbound.orders.repository;

import com.tekclover.wms.api.inbound.orders.model.unallocatedorder.UnallocatedOrderLine;
import com.tekclover.wms.api.inbound.orders.model.unallocatedorder.UnallocatedOrderLineV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UnallocatedOrderLineRepository extends JpaRepository<UnallocatedOrderLineV2, Long>,
        JpaSpecificationExecutor<UnallocatedOrderLineV2> {

    public UnallocatedOrderLineV2 findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndProposedStorageBinAndProposedPackBarCodeAndDeletionIndicator(
            String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
            String itemCode, String proposedStorageBin, String proposedPackBarCode, Long deletionIndicator);

    Optional<UnallocatedOrderLineV2> findTopByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndStatusIdAndDeletionIndicatorOrderByPickupCreatedOnDesc(
            String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, Long statusId, Long deletionIndicator);


    @Query(value = "select unl.* from tblunallocatedorderline unl \n" +
            "where unl.is_deleted =0 and EXISTS(select 1 from tblgrline gr where gr.c_id = unl.c_id and gr.plant_id = unl.plant_id and \n" +
            "gr.lang_id = unl.lang_id and gr.wh_id = unl.wh_id and gr.itm_code= unl.itm_code and gr.is_deleted = 0)", nativeQuery = true)
    List<UnallocatedOrderLine> getUnAll();
}
