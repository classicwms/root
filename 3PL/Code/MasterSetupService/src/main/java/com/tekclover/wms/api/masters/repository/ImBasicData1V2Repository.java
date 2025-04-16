package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.v2.ImBasicData1V2;
import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ImBasicData1V2Repository extends JpaRepository<ImBasicData1V2, Long>,
        JpaSpecificationExecutor<ImBasicData1V2>,
        StreamableJpaSpecificationRepository<ImBasicData1V2> {

    @Query(value = "select \n"
            + "tw.lang_id AS languageId, \n"
            + "tw.wh_id AS warehouseId \n"
            + "from tblwarehouseid tw \n"
            + "where \n"
            + "tw.c_id IN (:companyCodeId) and \n"
            + "tw.plant_id IN (:plantId) and is_deleted = 0", nativeQuery = true)
    IKeyValuePair getImBasicDataV2Description(@Param(value = "companyCodeId") String companyCodeId,
                                              @Param(value = "plantId") String plantId);

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

    Optional<ImBasicData1V2> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long deletionIndicator);

    Optional<ImBasicData1V2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String itemCode, String manufacturerName, String languageId, Long deletionIndicator);

    Optional<ImBasicData1V2> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndUomIdAndManufacturerPartNoAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String itemCode, String uomId, String manufacturerPartNo, String languageId, long l);
}
