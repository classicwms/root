package com.tekclover.wms.api.outbound.transaction.repository;




import com.tekclover.wms.api.outbound.transaction.model.common.DbConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DbConfigRepository extends JpaRepository<DbConfig, Long> {


    @Query("SELECT dbName from DbConfig where companyCode =?1 and plantId=?2 and warehouseId =?3")
    String getDbName(String companyCodeId, String plantId, String warehouseId);

    @Query("SELECT dbName from DbConfig where companyCode IN ?1 and plantId IN ?2 and warehouseId IN ?3")
    String getDbList(List<String> companyCodeId, List<String> plantId, List<String> warehouseId);

    @Query(value = "SELECT top 1 db_name from db_config where warehouse_id = :warehouseId", nativeQuery = true)
    String getDbByWarehouse(@Param("warehouseId") String warehouseId);

    @Query(value = "SELECT top 1 db_name from db_config where warehouse_id in (:warehouseId)", nativeQuery = true)
    String getDbByWarehouseIn(@Param("warehouseId") List<String> warehouseId);


//    @Query("SELECT dbName from User where dbId =?1")
//    String getDbName(Long id);
}
