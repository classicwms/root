package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.IKeyValuePair;
import com.tekclover.wms.api.masters.model.threepl.billing.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface BillingRepository extends JpaRepository<Billing,String>, JpaSpecificationExecutor<Billing> {
    Optional<Billing> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPartnerCodeAndModuleIdAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String partnerCode, String moduleId, String languageId, Long deletionIndicator);


    @Query(value ="select  tl.bill_mode_id AS billingModeId,tl.bill_mode_text AS description \n"+
            " from tblbillingmodeid tl \n" +
            "WHERE \n"+
            "tl.bill_mode_id IN (:billingModeId) and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n"+
            "tl.is_deleted=0 ",nativeQuery = true)

    public IKeyValuePair getBillingModeIdAndDescription(@Param(value="billingModeId") Long billingModeId,
                                                   @Param(value = "languageId")String languageId,
                                                        @Param(value = "companyCodeId")String companyCodeId,
                                                        @Param(value = "plantId")String plantId,
                                                        @Param(value = "warehouseId")String warehouseId);


    @Query(value ="select  tl.bill_freq_id AS billFrequencyId,tl.bill_freq_text AS description \n"+
            " from tblbillingfrequencyid tl \n" +
            "WHERE \n"+
            "tl.bill_freq_id IN (:billFrequencyId) and tl.lang_id IN (:languageId) and  tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and \n"+
            "tl.is_deleted=0 ",nativeQuery = true)

    public IKeyValuePair getBillFrequencyIdAndDescription(@Param(value="billFrequencyId") Long billFrequencyId,
                                                        @Param(value = "languageId")String languageId,
                                                          @Param(value = "companyCodeId")String companyCodeId,
                                                          @Param(value = "plantId")String plantId,
                                                          @Param(value = "warehouseId")String warehouseId);

    @Query(value ="select  tl.payment_term_id AS paymentTermId,tl.payment_term_text AS description \n"+
            " from tblpaymenttermid tl \n" +
            "WHERE \n"+
            "tl.payment_term_id IN (:paymentTermId) and tl.lang_id IN (:languageId) and tl.wh_id IN (:warehouseId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and \n"+
            "tl.is_deleted=0 ",nativeQuery = true)

    public IKeyValuePair getPaymentTermIdAndDescription(@Param(value="paymentTermId") Long paymentTermId,
                                                        @Param(value = "languageId")String languageId,
                                                        @Param(value = "companyCodeId")String companyCodeId,
                                                        @Param(value = "plantId")String plantId,
                                                        @Param(value = "warehouseId")String warehouseId);
    @Query(value ="select  tl.payment_mode_id AS paymentModeId,tl.payment_mode_text AS description \n"+
            " from tblpaymentmodeid tl \n" +
            "WHERE \n"+
            "tl.payment_mode_id IN (:paymentModeId) and tl.lang_id IN (:languageId) and tl.wh_id IN (:warehouseId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and \n"+
            "tl.is_deleted=0 ",nativeQuery = true)

    public IKeyValuePair getPaymentModeIdAndDescription(@Param(value="paymentModeId") Long paymentModeId,
                                                        @Param(value = "languageId")String languageId,
                                                        @Param(value = "companyCodeId")String companyCodeId,
                                                        @Param(value = "plantId")String plantId,
                                                        @Param(value = "warehouseId")String warehouseId);
}
