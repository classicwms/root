package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.threepl.pricelist.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PriceListRepository extends JpaRepository<PriceList, Long>, JpaSpecificationExecutor<PriceList> {
    Optional<PriceList> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndPriceListIdAndServiceTypeIdAndChargeRangeIdAndLanguageIdAndDeletionIndicator(String companyCodeId, String plantId, String warehouseId, String moduleId, Long priceListId, Long serviceTypeId, Long chargeRangeId, String languageId, Long deletionIndicator);

    // PriceList findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndLanguageIdAndDeletionIndicator(String companyCodeId,String plantId,String warehouseId,Long priceListId,String languageId,Long deletionIndicator);
    //  Optional<PriceList> findByCompanyCodeIdAndPlantIdAnWarehouseIdAndDeletionIndicator(String companyCodeId, String plantId, String warehouseId, Long deletionIndicator);


    @Query(value = "select  tl.c_id AS companyCodeId,tl.c_text AS description\n" +
            " from tblcompanyid tl \n" +
            "WHERE \n" +
            "tl.c_id IN (:companyCodeId) and tl.lang_id IN (:languageId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public IKeyValuePair getCompanyIdAndDescription(@Param(value = "companyCodeId") String companyCodeId,
                                                    @Param(value = "languageId") String languageId);


    @Query(value = "select  tl.plant_id AS plantId,tl.plant_text AS description\n" +
            " from tblplantid tl \n" +
            "WHERE \n" +
            "tl.plant_id IN (:plantId)and tl.lang_id IN (:languageId) and tl.c_id IN(:companyCodeId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public IKeyValuePair getPlantIdAndDescription(@Param(value = "plantId") String plantId,
                                                  @Param(value = "languageId") String languageId,
                                                  @Param(value = "companyCodeId") String companyCodeId);

    @Query(value = "select  tl.wh_id AS warehouseId,tl.wh_text AS description\n" +
            " from tblwarehouseid tl \n" +
            "WHERE \n" +
            "tl.wh_id IN (:warehouseId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public IKeyValuePair getWarehouseIdAndDescription(@Param(value = "warehouseId") String warehouseId,
                                                      @Param(value = "languageId") String languageId,
                                                      @Param(value = "companyCodeId") String companyCodeId,
                                                      @Param(value = "plantId") String plantId);


    @Query(value = "select  tl.ser_typ_id AS serviceTypeId,tl.ser_typ_text AS description\n" +
            " from tblservicetypeid tl \n" +
            "WHERE \n" +
            "tl.ser_typ_id IN (:serviceTypeId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.mod_id IN (:moduleId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public IKeyValuePair getServiceTypeIdAndDescription(@Param(value = "serviceTypeId") Long serviceTypeId,
                                                        @Param(value = "languageId") String languageId,
                                                        @Param(value = "companyCodeId") String companyCodeId,
                                                        @Param(value = "plantId") String plantId,
                                                        @Param(value = "moduleId") String moduleId,
                                                        @Param(value = "warehouseId") String warehouseId);

    @Query(value = "select top 1 tl.mod_id AS moduleId,tl.module_text AS description \n" +
            " from tblmoduleid tl \n" +
            "WHERE \n" +
            "tl.mod_id IN (:moduleId) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN(:plantId) and \n" +
            "tl.wh_id IN (:warehouseId) and tl.is_deleted=0 ", nativeQuery = true)

    public IKeyValuePair getModuleIdAndDescription(@Param(value = "moduleId") String moduleId,
                                                   @Param(value = "languageId") String languageId,
                                                   @Param(value = "companyCodeId") String companyCodeId,
                                                   @Param(value = "plantId") String plantId,
                                                   @Param(value = "warehouseId") String warehouseId);

    @Query(value = "select  tl.price_list_id AS priceListId,tl.dec AS description \n" +
            " from tblpricelist tl \n" +
            "WHERE \n" +
            "tl.price_list_id IN (:priceListId) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and " +
            " tl.ser_typ_id IN (:serviceTypeId) and tl.mod_id IN (:moduleId) and tl.charge_range_id IN (:chargeRangeId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public IKeyValuePair getPriceListIdAndDescription(@Param(value = "priceListId") Long priceListId,
                                                      @Param(value = "languageId") String languageId,
                                                      @Param(value = "companyCodeId") String companyCodeId,
                                                      @Param(value = "plantId") String plantId,
                                                      @Param(value = "warehouseId") String warehouseId,
                                                      @Param(value = "moduleId") String moduleId,
                                                      @Param(value = "serviceTypeId") Long serviceTypeId,
                                                      @Param(value = "chargeRangeId") Long chargeRangeId);


    List<PriceList> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndServiceTypeIdAndModuleIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long priceListId, Long serviceTypeId, String moduleId, Long deletionIndicator);

    List<PriceList> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPriceListIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long priceListId, Long deletionIndicator);

    //Delete PriceList
    @Modifying
    @Query(value = "UPDATE tblpricelist SET IS_DELETED = 1 WHERE c_id IN (:companyCodeId) AND lang_id IN (:languageId) AND plant_id IN (:plantId) and " +
            "price_list_id IN (:priceListId) And wh_id IN (:warehouseId) and ser_typ_id IN (:serviceTypeId) and mod_id IN (:moduleId) and charge_range_id IN (:chargeRangeId) AND IS_DELETED = 0", nativeQuery = true)
    void deletepriceList(@Param(value = "priceListId") Long priceListId,
                         @Param(value = "languageId") String languageId,
                         @Param(value = "companyCodeId") String companyCodeId,
                         @Param(value = "plantId") String plantId,
                         @Param(value = "warehouseId") String warehouseId,
                         @Param(value = "moduleId") String moduleId,
                         @Param(value = "serviceTypeId") Long serviceTypeId,
                         @Param(value = "chargeRangeId") Long chargeRangeId);


    // Gr Invoice

    @Query(value = "select SUM(TOTAL_TPL_CBM) totalCbm, sum(RATE) totalRate, min(currency) currency from tblgrline where c_id = :companyId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and \n" +
            "partner_code = :partnerCode and is_deleted = 0 and GR_INVOICE = 0 and gr_ctd_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) ", nativeQuery = true)
    public IKeyValuePair getCbm(@Param("companyId") String companyId,
                         @Param("plantId") String plantId,
                         @Param("languageId") String languageId,
                         @Param("warehouseId") String warehouseId,
                         @Param("partnerCode") String partnerCode,
                         @Param("fromDate") Date fromDate,
                         @Param("toDate") Date toDate);

//    @Modifying
//    @Query(value = "UPDATE tblgrline SET GR_INVOICE = 1 where c_id = :companyId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and \n" +
//            "partner_code = :partnerCode and is_deleted = 0 and GR_INVOICE = 0 and gr_ctd_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) ", nativeQuery = true)
//    public void updateGrInvoice(@Param("companyId") String companyId,
//                                @Param("plantId") String plantId,
//                                @Param("languageId") String languageId,
//                                @Param("warehouseId") String warehouseId,
//                                @Param("partnerCode") String partnerCode,
//                                @Param("fromDate") Date fromDate,
//                                @Param("toDate") Date toDate);
//
//
//// PutAway Invoice
//
//    @Query(value = "select SUM(TOTAL_TPL_CBM) totalCbm, sum(RATE) totalRate, min(currency) currency from tblputawayheader " +
//            "where c_id = :companyId and plant_id = :plantId " +
//            "and wh_id = :warehouseId and partner_code = :partnerCode and PUTAWAY_INVOICE = 0 and " +
//            "pa_ctd_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) " +
//            "and is_deleted = 0", nativeQuery = true)
//    Double getPutAwayCbm(@Param("companyId") String companyId,
//                         @Param("plantId") String plantId,
//                         @Param("languageId") String languageId,
//                         @Param("warehouseId") String warehouseId,
//                         @Param("partnerCode") String partnerCode,
//                         @Param("fromDate") Date fromDate,
//                         @Param("toDate") Date toDate);
//
//    @Modifying
//    @Query(value = "UPDATE tblputawayheader SET PUTAWAY_INVOICE = 1 where c_id = :companyId and plant_id = :plantId and lang_id = :languageId and wh_id = :warehouseId and \n" +
//            "partner_code = :partnerCode and is_deleted = 0 and PUTAWAY_INVOICE = 0 and pa_ctd_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) ", nativeQuery = true)
//    public void updatePutAwayInvoice(@Param("companyId") String companyId,
//                                     @Param("plantId") String plantId,
//                                     @Param("languageId") String languageId,
//                                     @Param("warehouseId") String warehouseId,
//                                     @Param("partnerCode") String partnerCode,
//                                     @Param("fromDate") Date fromDate,
//                                     @Param("toDate") Date toDate);
//
//    //StockMovement Invoice
//
//    @Query(value = "select SUM(TOTAL_TPL_CBM) totalCbm, sum(RATE) totalRate, min(currency) currency from tblstockmovement " + "where c_id = :companyId " + "and plant_id = :plantId and wh_id = :warehouseId " +
//            "and partner_code = :partnerCode " +
//            "and is_deleted = 0 and CL_STOCK = 0 and ctd_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) ", nativeQuery = true)
//    public Double getclosestock(@Param("companyId") String companyId,
//                                @Param("plantId") String plantId,
//                                @Param("warehouseId") String warehouseId,
//                                @Param("partnerCode") String partnerCode,
//                                @Param("fromDate") Date fromDate,
//                                @Param("toDate") Date toDate);
//
//    @Modifying
//    @Query(value = "UPDATE tblstockmovement SET STOCK_INVOICE = 1 " + "where c_id = :companyId " + "and plant_id = :plantId and wh_id = :warehouseId " +
//            "and partner_code = :partnerCode " +
//            "and is_deleted = 0 and STOCK_INVOICE = 0 and ctd_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) ", nativeQuery = true)
//    public void updateCloseStock(@Param("companyId") String companyId,
//                                 @Param("plantId") String plantId,
//                                 @Param("warehouseId") String warehouseId,
//                                 @Param("partnerCode") String partnerCode,
//                                 @Param("fromDate") Date fromDate,
//                                 @Param("toDate") Date toDate);
//
//    //PickUp Invoice
//
//    @Query(value = "select SUM(TOTAL_TPL_CBM) totalCbm, sum(RATE) totalRate, min(currency) currency from tblpickupline " +
//            "where c_id = :companyId and plant_id = :plantId " +
//            "and wh_id = :warehouseId and partner_code = :partnerCode " +
//            "and is_deleted = 0 and PICKING_INVOICE = 0 and pick_ctd_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) ", nativeQuery = true)
//    Double getPickCbm(@Param("companyId") String companyId,
//                      @Param("plantId") String plantId,
//                      @Param("warehouseId") String warehouseId,
//                      @Param("partnerCode") String partnerCode,
//                      @Param("fromDate") Date fromDate,
//                      @Param("toDate") Date toDate);
//
//    @Modifying
//    @Query(value = "UPDATE tblpickupline SET PICKING_INVOICE = 1" +
//            "where c_id = :companyId and plant_id = :plantId " +
//            "and wh_id = :warehouseId and partner_code = :partnerCode " +
//            "and is_deleted = 0 and PICKING_INVOICE = 0 and pick_ctd_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) ", nativeQuery = true)
//    public void updatePickCbm(@Param("companyId") String companyId,
//                              @Param("plantId") String plantId,
//                              @Param("warehouseId") String warehouseId,
//                              @Param("partnerCode") String partnerCode,
//                              @Param("fromDate") Date fromDate,
//                              @Param("toDate") Date toDate);
//
//    //Packing Invoice
//
//    @Query(value = "select SUM(TOTAL_TPL_CBM) totalCbm, sum(RATE) totalRate, min(currency) currency from tblpackingline " +
//            "where  c_id = :companyId and plant_id = :plantId " +
//            "and wh_id = :warehouseId and partner_code = :partnerCode " +
//            "and is_deleted = 0 and PACKING_INVOICE = 0 and pack_cnf_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) ", nativeQuery = true)
//    Double getPackCbm(@Param("companyId") String companyId,
//                      @Param("plantId") String plantId,
//                      @Param("warehouseId") String warehouseId,
//                      @Param("partnerCode") String partnerCode,
//                      @Param("fromDate") Date fromDate,
//                      @Param("toDate") Date toDate);
//
//    @Modifying
//    @Query(value = "UPDATE tblpackingline SET PACKING_INVOICE = 1 " +
//            "where  c_id = :companyId and plant_id = :plantId " +
//            "and wh_id = :warehouseId and partner_code = :partnerCode " +
//            "and is_deleted = 0 and PACKING_INVOICE = 0 and pack_cnf_on between coalesce(convert(varchar(255), :fromDate), null) and coalesce(convert(varchar(255), :toDate), null) ", nativeQuery = true)
//    public void updatePackCbm(@Param("companyId") String companyId,
//                              @Param("plantId") String plantId,
//                              @Param("warehouseId") String warehouseId,
//                              @Param("partnerCode") String partnerCode,
//                              @Param("fromDate") Date fromDate,
//                              @Param("toDate") Date toDate);

}
