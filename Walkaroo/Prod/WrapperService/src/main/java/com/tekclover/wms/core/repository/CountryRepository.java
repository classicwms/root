package com.tekclover.wms.core.repository;

import com.tekclover.wms.core.model.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long>, JpaSpecificationExecutor<Country> {

    List<Country> findAll();
    Optional<Country> findByCountryIdAndLanguageId (String countryId,String languageId);

}