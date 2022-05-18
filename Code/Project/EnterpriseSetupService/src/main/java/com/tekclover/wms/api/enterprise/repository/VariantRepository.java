package com.tekclover.wms.api.enterprise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.enterprise.model.variant.Variant;

@Repository
@Transactional
public interface VariantRepository extends JpaRepository<Variant,Long>, JpaSpecificationExecutor<Variant> {

	public List<Variant> findAll();
	public Optional<Variant> findByVariantId(String variantId);
}