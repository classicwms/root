package com.tekclover.wms.api.idmaster.repository;


import com.tekclover.wms.api.idmaster.model.common.DbConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DbConfigRepository extends JpaRepository<DbConfig, Long> {


    @Query(value = "SELECT db_name from db_config where c_id = :companyCodeId and plant_id= :plantId and warehouse_id = :warehouseId",nativeQuery = true)
    String getDbName(@Param("companyCodeId") String companyCodeId,
                     @Param("plantId") String plantId,
                     @Param("warehouseId") String warehouseId);


//    @Query("SELECT dbName from User where dbId =?1")
//    String getDbName(Long id);
}
