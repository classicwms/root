package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.dto.Inventory;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface InventoryRepository extends PagingAndSortingRepository<Inventory,Long>, JpaSpecificationExecutor<Inventory> {

	@Query( value = "select * \n" +
			"from tblinventory \n" +
			"where itm_code in (:itemCode) and wh_id in (:warehouseId) and\n" +
			"lang_id in (:languageId) and c_id in (:companyId) and plant_id in (:plantId)", nativeQuery = true)
	List<Inventory> updateInventory(@Param("itemCode") String itemCode,
									@Param("warehouseId") String warehouseId,
									@Param("languageId") String languageId,
									@Param("companyId") String companyId,
									@Param("plantId") String plantId);

	@Query( value = "select * \n" +
			"from tblinventory \n" +
			"where st_bin in (:storageBin) and wh_id in (:warehouseId) and \n" +
			"lang_id in (:languageId) and c_id in (:companyId) and plant_id in (:plantId)", nativeQuery = true)
	List<Inventory> updateInventoryBin(@Param("storageBin") String storageBin,
										@Param("warehouseId") String warehouseId,
										@Param("languageId") String languageId,
										@Param("companyId") String companyId,
										@Param("plantId") String plantId);
}