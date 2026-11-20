package com.tekclover.wms.api.idmaster.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.idmaster.model.interimbarcode.InterimBarcode;

@Repository
public interface InterimBarcodeRepository extends JpaRepository<InterimBarcode, Long>, JpaSpecificationExecutor<InterimBarcode> {

	Optional<InterimBarcode> findByStorageBinAndItemCodeAndBarcodeAndDeletionIndicator(String storageBin,
			String itemCode, String barcode, Long deletionIndicator);

    Optional<InterimBarcode> findByItemCode(String itemCode);
    Optional<InterimBarcode> findByItemCodeAndReferenceField1(String itemCode, String referenceField1);
}