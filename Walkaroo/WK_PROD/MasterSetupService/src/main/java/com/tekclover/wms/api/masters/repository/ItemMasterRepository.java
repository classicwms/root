package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.masters.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ItemMasterRepository extends JpaRepository<Item,String> {


	List<Item> findTopByProcessedStatusIdOrderByOrderReceivedOn(long l);

	List<Item> findByProcessedStatusIdOrderByOrderReceivedOn(long l);

    Item findBySku(String sku);

    List<Item> findByCompanyCodeAndBranchCodeAndSkuAndManufacturerName(String companyCode, String branchCode, String sku, String manufactureName);

    Item findTopByCompanyCodeAndBranchCodeAndSkuAndManufacturerNameOrderByOrderReceivedOnDesc(
            String companyCode, String branchCode, String sku, String manufactureName);

    Item findTopByCompanyCodeAndBranchCodeAndSkuAndManufacturerNameAndProcessedStatusIdOrderByOrderReceivedOnDesc(
            String companyCodeId, String plantId, String itemCode, String manufacturerName, long l);

    Item findTopByCompanyCodeAndBranchCodeAndSkuAndManufacturerNameAndProcessedStatusIdOrderByOrderReceivedOn(
            String companyCodeId, String plantId, String itemCode, String manufacturerName, long l);
}



