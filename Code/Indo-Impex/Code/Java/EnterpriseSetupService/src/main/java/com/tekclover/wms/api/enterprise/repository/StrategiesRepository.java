package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.strategies.Strategies;
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
public interface StrategiesRepository extends JpaRepository<Strategies, Long>, JpaSpecificationExecutor<Strategies> {

    public List<Strategies> findAll();

    public Optional<Strategies> findByStrategyTypeId(Long strategyTypeId);

    public List<Strategies> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndSequenceIndicatorAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, Long strategyTypeId,
            Long sequenceIndicator, Long deletionIndicator);

    public List<Strategies> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndDeletionIndicator(
            String languageId, String companyCode, String plantId, String warehouseId, Long deletionIndicator);

    @Query(value = "select tl.str_typ_text AS description,tl.str_typ_id AS strategyTypeId  \n" +
            " from tblstrategyid tl \n" +
            "WHERE \n" +
            "tl.str_typ_id IN (:strategyTypeId) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public List<IkeyValuePair> getStrategyIdAndDescription(@Param(value = "strategyTypeId") Long strategyTypeId,
                                                           @Param(value = "languageId") String languageId,
                                                           @Param(value = "companyCodeId") String companyCodeId,
                                                           @Param(value = "plantId") String plantId,
                                                           @Param(value = "warehouseId") String warehouseId);


    //Description
    @Query(value = "select tc.c_text companyDescription,\n" +
            "tp.plant_text plantDescription,\n" +
            "tw.wh_text warehouseDescription from \n" +
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
    public IkeyValuePair getDescription(@Param(value = "companyCodeId") String companyCodeId,
                                        @Param(value = "languageId") String languageId,
                                        @Param(value = "plantId") String plantId,
                                        @Param(value = "warehouseId") String warehouseId);

}