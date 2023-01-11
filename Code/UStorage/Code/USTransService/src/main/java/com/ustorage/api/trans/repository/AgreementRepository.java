package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import com.ustorage.api.trans.model.agreement.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long>,
		JpaSpecificationExecutor<Agreement> {

	public List<Agreement> findAll();

	public Optional<Agreement> findByAgreementNumberAndDeletionIndicator(String agreementId, long l);

	@Query(value = "SELECT * \r\n"
			+ "FROM tblagreement \r\n"
			+ "WHERE \r\n"
			+ "(COALESCE(:agreementNumber,null) IS NULL OR (tblagreement.AGREEMENT_NUMBER IN (:agreementNumber))) \n"
			+ "AND tblagreement.IS_DELETED=0", nativeQuery = true)
	public Agreement getAgreement(@Param("agreementNumber") String agreementNumber);

}