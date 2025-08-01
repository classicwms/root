package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.threepl.servicetypeid.ServiceTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Repository
@Transactional
public interface ServiceTypeIdRepository extends JpaRepository<ServiceTypeId,Long>, JpaSpecificationExecutor<ServiceTypeId> {
    Optional<ServiceTypeId> findByCompanyCodeIdAndPlantIdAndWarehouseIdAndModuleIdAndServiceTypeIdAndLanguageIdAndDeletionIndicator(
            String companyCodeId, String plantId, String warehouseId, String moduleId, Long serviceTypeId, String languageId,  Long deletionIndicator);

    @Query(value = "select STRING_AGG(code_id,', ') \n" +
            "from tblstorageunit tsu \n" +
            "join tblstorenumber ts on ts.store_number=tsu.item_code\n"+
            " where \n" +
            "(COALESCE(:agreementNumber,null) IS NULL OR (ts.AGREEMENT_NUMBER IN (:agreementNumber))) and\n"+
            "ts.is_deleted=0 ",nativeQuery = true)
    public String getStoreNumber(@Param("agreementNumber") String agreementNumber);

}
