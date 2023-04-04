package com.tekclover.wms.api.idmaster.repository;

import com.tekclover.wms.api.idmaster.model.email.EMailDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface EMailDetailsRepository extends JpaRepository<EMailDetails,String>, JpaSpecificationExecutor<EMailDetails> {

    public List<EMailDetails> findAll();
   Optional<EMailDetails> findByIdAndDeletionIndicator(Long id, Long deletionIndicator);
   Optional<EMailDetails> findById(Long id);

    @Query(value = "select te.to_address,te.cc_address) \n" +
            "from tblemail te \n" +
            " where \n" +
            "(COALESCE(:sNo,null) IS NULL OR (te.S_NO IN (:sNo))) \n"+
            "te.is_deleted=0 ",nativeQuery = true)
    public EMailDetails getEmail(@Param("sNo") Long sNo);

//    @Query(value = "select STRING_AGG(te.to_address,', ') \n" +
//            "from tblemail te \n" +
//            " where \n" +
//            "te.is_deleted=0 ",nativeQuery = true)
//    public String getToAddress();
@Query(value = "select te.to_address \n" +
        "from tblemail te \n" +
        " where \n" +
        "te.is_deleted=0 ",nativeQuery = true)
public EMailDetails getToAddress();

//    @Query(value = "select STRING_AGG(te.cc_address,', ') \n" +
//            "from tblemail te \n" +
//            " where \n" +
//            "te.is_deleted=0 ",nativeQuery = true)
//    public String getCcAddress();

    @Query(value = "select te.cc_address \n" +
            "from tblemail te \n" +
            " where \n" +
            "te.is_deleted=0 ",nativeQuery = true)
    public EMailDetails getCcAddress();
}
