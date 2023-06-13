package com.tekclover.wms.api.masters.repository;

import java.util.List;
import java.util.Optional;

import com.tekclover.wms.api.masters.model.dto.Inventory;
import com.tekclover.wms.api.masters.model.impl.ItemListImpl;
import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;

@Repository
@Transactional
public interface ImBasicData1Repository extends PagingAndSortingRepository<ImBasicData1,Long>, 
JpaSpecificationExecutor<ImBasicData1>, StreamableJpaSpecificationRepository<ImBasicData1> {

	public Optional<ImBasicData1> findByItemCodeAndWarehouseIdAndDeletionIndicator(String itemCode, String warehouseId, Long deletionIndicator);
	public List<ImBasicData1> findByItemCodeLikeAndDescriptionLike(String itemCode, String itemDesc);


	@Query(
			value = "select TOP 50 itm_code as itemCode ,text as description from tblimbasicdata1 \n" +
					"where ( itm_code like :searchText1% or itm_code like %:searchText2 \n" +
					"or text like %:searchText3% ) \n" +
					"group by itm_code,text ",
			nativeQuery = true
	)
	List<ItemListImpl> getItemListBySearch(@Param("searchText1") String searchText1,
										   @Param("searchText2") String searchText2,
										   @Param("searchText3") String searchText3);
}