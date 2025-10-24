package com.tekclover.wms.api.outbound.transaction.repository;

import com.tekclover.wms.api.outbound.transaction.model.deliveryheader.DeliveryHeader;
import com.tekclover.wms.api.outbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.outbound.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DeliveryHeaderRepository extends JpaRepository<DeliveryHeader, String>,
        JpaSpecificationExecutor<DeliveryHeader>, StreamableJpaSpecificationRepository<DeliveryHeader> {

    Optional<DeliveryHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndDeliveryNoAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long deliveryNo, String languageId, Long deletionIndicator);

    List<DeliveryHeader> findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndDeliveryNoAndDeletionIndicator(
            String languageId, String companyCodeId, String plantId, String warehouseId, Long deliveryNo, Long deletionIndicator);


    @Query(value = "select MAX(DLV_NO)+1 \n" +
            "from tbldeliveryheader",nativeQuery = true)
    public Long getDeliveryNo();

    List<DeliveryHeader> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String refDocNumber, String languageId, Long deletionIndicator);

    //Description
    @Query(value = "select tc.c_text companyDesc,\n" +
            "tp.plant_text plantDesc,\n" +
            "tw.wh_text warehouseDesc from \n" +
            "tblcompanyid tc\n" +
            "join tblplantid tp on tp.c_id = tc.c_id and tp.lang_id = tc.lang_id\n" +
            "join tblwarehouseid tw on tw.c_id = tc.c_id and tw.lang_id=tc.lang_id and tw.plant_id = tp.plant_id\n" +
            "where\n" +
            "tc.c_id IN (:companyCodeId) and \n" +
            "tc.lang_id IN (:languageId) and \n" +
            "tp.plant_id IN(:plantId) and \n" +
            "tw.wh_id IN (:warehouseId) and \n" +
            "tc.is_deleted=0 and \n" +
            "tp.is_deleted=0 and \n" +
            "tw.is_deleted=0", nativeQuery = true)
    public IKeyValuePair getDescription(@Param(value = "companyCodeId") String companyCodeId,
                                        @Param(value = "languageId") String languageId,
                                        @Param(value = "plantId") String plantId,
                                        @Param(value = "warehouseId") String warehouseId);

    //Status Description
    @Query(value = "select status_text \n" +
            "from tblstatusid \n" +
            "where \n" +
            "status_id IN (:statusId) and \n" +
            "lang_id IN (:languageId) and \n" +
            "is_deleted=0", nativeQuery = true)
    public String getStatusDescription(@Param(value = "statusId") Long statusId,
                                       @Param(value = "languageId") String languageId);
}
