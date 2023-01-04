package com.ustorage.api.trans.repository;

import com.ustorage.api.trans.model.consumablepurchase.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsumablePurchaseRepository extends JpaRepository<ConsumablePurchase, Long>,
		JpaSpecificationExecutor<ConsumablePurchase> {

	public List<ConsumablePurchase> findAll();

	public List<ConsumablePurchase> findByItemCodeAndDeletionIndicator(String itemCode, long l);

	public Optional<ConsumablePurchase> findByItemCodeAndReceiptNoAndDeletionIndicator(String itemCode, String receiptNo, long l);


}