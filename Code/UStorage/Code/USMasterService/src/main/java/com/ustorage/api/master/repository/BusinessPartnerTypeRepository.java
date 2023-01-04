package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.businesspartnertype.BusinessPartnerType;

@Repository
public interface BusinessPartnerTypeRepository extends JpaRepository<BusinessPartnerType, Long>{

	public List<BusinessPartnerType> findAll();

	public Optional<BusinessPartnerType> findByCodeAndDeletionIndicator(String businessPartnerTypeId, long l);
}