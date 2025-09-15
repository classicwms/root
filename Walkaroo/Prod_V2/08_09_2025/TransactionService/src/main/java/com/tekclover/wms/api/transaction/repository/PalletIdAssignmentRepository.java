package com.tekclover.wms.api.transaction.repository;

import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PalletIdAssignment;
import com.tekclover.wms.api.transaction.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
@Transactional
public interface PalletIdAssignmentRepository extends JpaRepository<PalletIdAssignment, Long>, JpaSpecificationExecutor<PalletIdAssignment>, StreamableJpaSpecificationRepository<PalletIdAssignment> {

    Optional<PalletIdAssignment> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndPaIdAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, Long pId, String languageId, Long deletionIndicator);

    Optional<PalletIdAssignment> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPalletIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId, String palletId, Long deletionIndicator);

    boolean existsByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndPalletIdAndStatusIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String languageId, String palletId, Long statusId, Long deletionIndicator);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("Update PalletIdAssignment ob SET ob.statusId = :statusId, ob.statusDescription = :statusDescription, ob.confirmedBy = :confirmedBy, ob.confirmedOn = :confirmedOn \r\n "
            + " WHERE ob.companyCodeId = :companyCodeId AND ob.plantId = :plantId AND ob.languageId = :languageId AND ob.warehouseId = :warehouseId AND ob.palletId = :palletId")
    public void updatePalletIdAssignment(@Param("companyCodeId") String companyCodeId,
                                         @Param("plantId") String plantId,
                                         @Param("languageId") String languageId,
                                         @Param("warehouseId") String warehouseId,
                                         @Param("palletId") String palletId,
                                         @Param("confirmedBy") String confirmedBy,
                                         @Param("confirmedOn") Date confirmedOn,
                                         @Param("statusId") Long statusId,
                                         @Param("statusDescription") String statusDescription);

    @Query(value = "SELECT top 1 PA_ASGN_ON FROM tblpalletidassignment WHERE "
            + "(:palletId IS NULL OR PAL_ID IN (:palletId)) AND "
            + "(:companyCodeId IS NULL OR c_id IN (:companyCodeId)) AND "
            + "(:plantId IS NULL OR plant_id IN (:plantId)) AND "
            + "(:languageId IS NULL OR lang_id IN (:languageId)) AND "
            + "(:warehouseId IS NULL OR wh_id IN (:warehouseId)) AND "
            + "is_deleted = 0 and STATUS_ID = 0 order by PA_CTD_ON DESC", nativeQuery = true)
    public Date getAssignedOn(@Param("companyCodeId") String companyCodeId,
                              @Param("plantId") String plantId,
                              @Param("languageId") String languageId,
                              @Param("warehouseId") String warehouseId,
                              @Param("palletId") String palletId);
}