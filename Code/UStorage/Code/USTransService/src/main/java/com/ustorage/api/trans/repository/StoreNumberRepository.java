package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import com.ustorage.api.trans.model.agreement.*;
import com.ustorage.api.trans.model.storenumber.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.stereotype.Repository;

@Repository
public interface StoreNumberRepository extends JpaRepository<StoreNumber, Long>,
        JpaSpecificationExecutor<StoreNumber>{

    public List<StoreNumber> findAll();

    public Optional<StoreNumber> findByStoreNumberAndDeletionIndicator(String storeNumberId, long l);


    List<StoreNumber> findByAgreementNumberAndDeletionIndicator(String agreementNumber, long l);

}
