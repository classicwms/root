package com.tekclover.wms.api.masters.repository;



import com.tekclover.wms.api.masters.model.common.DbConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DbConfigRepository extends JpaRepository<DbConfig, Long> {


    @Query(value = "SELECT db_name from db_config where c_id = :companyCodeId and plant_id= :plantId and warehouse_id = :warehouseId",nativeQuery = true)
    String getDbName(@Param("companyCodeId") String companyCodeId,
                     @Param("plantId") String plantId,
                     @Param("warehouseId") String warehouseId);

    @Query("SELECT dbName from DbConfig where companyCode IN ?1 and plantId IN ?2 and warehouseId IN ?3")
    String getDbList(List<String> companyCodeId, List<String> plantId, List<String>warehouseId);



    @Query("SELECT dbName from DbConfig where companyCode IN ?1 and plantId IN ?2 and warehouseId IN ?3")
    String getDbNameList(List<String> companyCodeId, List<String> plantId, List<String> warehouseId);

    @Query("SELECT dbName from DbConfig where companyCode =?1 and plantId=?2")
    String getDbNameWithoutWhId(String companyCode, String branchCode);

//    @Query("SELECT dbName from User where dbId =?1")
//    String getDbName(Long id);
}
