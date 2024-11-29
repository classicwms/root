package com.tekclover.wms.api.enterprise.transaction.repository;

import com.tekclover.wms.api.enterprise.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.enterprise.transaction.model.dto.ImBasicData1;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public interface ImBasicData1Repository extends PagingAndSortingRepository<ImBasicData1,Long>, JpaSpecificationExecutor<ImBasicData1> {

	@Query (value = "SELECT TEXT AS description, MFR_PART AS manufacturePart FROM tblimbasicdata1 \r\n"
			+ " WHERE ITM_CODE = :itemCode", nativeQuery = true)
	public List<IImbasicData1> findByItemCode (@Param(value = "itemCode") String itemCode);

	public ImBasicData1 findByItemCodeAndWarehouseIdInAndDeletionIndicator(String itemCode, List<String> warehouseId, long l);
	
	public ImBasicData1 findByItemCodeAndWarehouseIdAndDeletionIndicator(String itemCode, String warehouseId, long l);
}