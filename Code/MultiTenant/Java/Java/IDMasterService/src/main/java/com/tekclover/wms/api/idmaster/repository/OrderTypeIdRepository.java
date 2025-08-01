package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.hhtuser.OrderTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface OrderTypeIdRepository extends JpaRepository<OrderTypeId,String>, JpaSpecificationExecutor<OrderTypeId> {

    List<OrderTypeId> findByUserIdAndDeletionIndicator(String userId, Long deletionIndicator);

    List<OrderTypeId> findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndUserIdAndDeletionIndicator(
            String companyCodeId, String plantId, String languageId, String warehouseId, String userId, Long deletionIndicator);

    @Modifying(clearAutomatically = true)
    @Query(value ="delete \n"+
            "tblordertypeid \n" +
            "WHERE \n"+
            "usr_id IN (:userId) and lang_id IN (:languageId) and c_id IN (:companyCodeId) and plant_id IN (:plantId) and \n"+
            "wh_id IN (:warehouseId) and is_deleted=0 ",nativeQuery = true)
    public void deleteOrderTypeId(@Param(value="userId") String userId,
                                  @Param(value = "languageId")String languageId,
                                  @Param(value = "companyCodeId")String companyCodeId,
                                  @Param(value = "plantId")String plantId,
                                  @Param(value = "warehouseId")String warehouseId);
}
