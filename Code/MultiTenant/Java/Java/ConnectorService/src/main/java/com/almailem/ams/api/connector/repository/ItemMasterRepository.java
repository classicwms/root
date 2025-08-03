package com.almailem.ams.api.connector.repository;

import com.almailem.ams.api.connector.model.master.ItemMaster;
import com.almailem.ams.api.connector.model.transferout.TransferOutHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface ItemMasterRepository extends JpaRepository<ItemMaster, String>, JpaSpecificationExecutor<ItemMaster> {

    List<ItemMaster> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    ItemMaster findByItemCode(String itemCode);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE ITEMMASTER set processedStatusId = 10, orderProcessedOn = :date  \r\n"
            + " WHERE Itemcode = :itemCode ", nativeQuery = true)
    public void updateProcessStatusIdWithDate (
            @Param(value = "itemCode") String itemCode,
            @Param(value = "date") Date date);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE ITEMMASTER set processedStatusId = :processedStatusId, orderProcessedOn = getdate()  \r\n"
            + " WHERE itemMasterId = :itemMasterId ", nativeQuery = true)
    public void updateProcessStatusId (
            @Param(value = "itemMasterId") Long itemMasterId,
            @Param(value = "processedStatusId") Long processedStatusId );

    @Query(value = "select * \n" +
            "from ITEMMASTER where Itemmasterid = :itemMasterId ",nativeQuery = true)
    public ItemMaster getItemMaster(@Param(value = "itemMasterId") Long itemMasterId);

    List<ItemMaster> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

    ItemMaster findTopByCompanyCodeAndBranchCodeAndItemCodeAndManufacturerShortNameOrderByOrderReceivedOnDesc(
            String companyCode, String branchCode, String itemCode, String manufacturerName);

    ItemMaster findTopByItemMasterIdAndCompanyCodeAndBranchCodeAndItemCodeAndManufacturerShortNameOrderByOrderReceivedOnDesc(
            Long itemMasterId, String companyCode, String branchCode, String itemCode, String manufacturerName);

    ItemMaster findTopByItemMasterIdAndCompanyCodeAndBranchCodeAndItemCodeAndManufacturerShortNameOrderByOrderReceivedOn(
            Long itemMasterId, String companyCode, String branchCode, String itemCode, String manufacturerName);

    @Query(value = "SELECT * FROM ITEMMASTER WHERE Processedstatusid = :statusId and Branchcode in (:branchCode) ORDER BY Orderreceivedon", nativeQuery = true)
    List<ItemMaster> findByItemMaster(@Param("statusId") Long statusId,
                                              @Param("branchCode") List<String> branchCode);
}
