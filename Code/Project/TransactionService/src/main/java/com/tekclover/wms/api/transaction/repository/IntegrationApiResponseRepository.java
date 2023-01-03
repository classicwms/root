package com.tekclover.wms.api.transaction.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tekclover.wms.api.transaction.model.integration.IntegrationApiResponse;

@Repository
@Transactional
public interface IntegrationApiResponseRepository extends JpaRepository<IntegrationApiResponse,Long> {

	public List<IntegrationApiResponse> findByOrderNumber (String orderNumber);
}