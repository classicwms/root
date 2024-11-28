package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.IkeyValuePair;
import com.tekclover.wms.api.enterprise.model.itemgroup.ItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ItemGroupRepository extends JpaRepository<ItemGroup, Long>, JpaSpecificationExecutor<ItemGroup> {
    public List<ItemGroup> findAll();

    public Optional<ItemGroup> findByItemGroupId(Long itemGroupId);

    public List<ItemGroup> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndItemGroupIdAndSubItemGroupIdAndDeletionIndicator(
            String languageId, String companyId, String plantId, String warehouseId, Long itemTypeId,
            Long itemGroupId, Long subItemGroupId, Long deletionIndicator);

    public List<ItemGroup> findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndAndDeletionIndicator(
            String languageId, String companyId, String plantId, String warehouseId, Long itemTypeId, Long deletionIndicator);

//	public ItemGroup findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndItemTypeIdAndAndDeletionIndicator(
//			String languageId, String companyId, String plantId, String warehouseId, Long itemTypeId,Long deletionIndicator);

    @Query(value = "select max(ID)+1 \n" +
            " from tblitemgroup ", nativeQuery = true)
    public Long getId();

    @Query(value = "select  tl.itm_grp_id AS itemGroupId,tl.imt_grp AS description\n" +
            " from tblitemgroupid tl \n" +
            "WHERE \n" +
            "tl.itm_grp_id IN (:itemGroupId)and tl.lang_id IN (:languageId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and tl.wh_id IN (:warehouseId) and tl.itm_type_id IN (:itemTypeId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)

    public IkeyValuePair getItemGroupIdAndDescription(@Param(value = "itemGroupId") Long itemGroupId,
                                                      @Param(value = "languageId") String languageId,
                                                      @Param(value = "companyCodeId") String companyCodeId,
                                                      @Param(value = "plantId") String plantId,
                                                      @Param(value = "warehouseId") String warehouseId,
                                                      @Param(value = "itemTypeId") Long itemTypeId);


    @Query(value = "select tl.sub_itm_grp_id AS subItemGroupId,tl.sub_itm_grp AS description\n" +
            " from tblsubitemgroupid tl \n" +
            "WHERE \n" +
            "tl.sub_itm_grp_id IN (:subItemGroupId) and tl.c_id IN (:companyCodeId) and tl.plant_id IN (:plantId) and \n" +
            "tl.wh_id IN (:warehouseId) and tl.itm_typ_id IN (:itemTypeId) and tl.itm_grp_id IN (:itemGroupId) and tl.lang_id IN (:languageId) and \n" +
            "tl.is_deleted=0 ", nativeQuery = true)
    public IkeyValuePair getSubItemGroupIdAndDescription(@Param(value = "subItemGroupId") Long subItemGroupId,
                                                         @Param(value = "companyCodeId") String companyCodeId,
                                                         @Param(value = "plantId") String plantId,
                                                         @Param(value = "warehouseId") String warehouseId,
                                                         @Param(value = "itemTypeId") Long itemTypeId,
                                                         @Param(value = "itemGroupId") Long itemGroupId,
                                                         @Param(value = "languageId") String languageId);
}