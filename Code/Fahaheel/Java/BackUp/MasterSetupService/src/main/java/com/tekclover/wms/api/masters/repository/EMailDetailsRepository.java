package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.email.EMailDetails;
import com.tekclover.wms.api.masters.repository.fragments.StreamableJpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface EMailDetailsRepository extends JpaRepository<EMailDetails, String>,
        JpaSpecificationExecutor<EMailDetails>, StreamableJpaSpecificationRepository<EMailDetails> {

    Optional<EMailDetails> findByEmailIdAndDeletionIndicator(Long emailId, Long deletionIndicator);

    @Query(value = "select te.to_address,te.cc_address) \n" +
            "from tblemail te \n" +
            "where \n" +
            "te.is_deleted=0 ", nativeQuery = true)
    public EMailDetails getEmail();

    @Query(value = "select te.to_address \n" +
            "from tblemail te \n" +
            "where \n" +
            "te.is_deleted=0 ", nativeQuery = true)
    public EMailDetails getToAddress();

    @Query(value = "select te.cc_address \n" +
            "from tblemail te \n" +
            "where \n" +
            "te.is_deleted=0 ", nativeQuery = true)
    public EMailDetails getCcAddress();
}
