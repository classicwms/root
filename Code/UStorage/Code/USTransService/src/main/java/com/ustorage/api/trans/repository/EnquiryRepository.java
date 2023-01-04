package com.ustorage.api.trans.repository;

import java.util.List;
import java.util.Optional;

import com.ustorage.api.trans.model.enquiry.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ustorage.api.trans.model.enquiry.Enquiry;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long>,
		JpaSpecificationExecutor<Enquiry> {

	public List<Enquiry> findAll();

	public Optional<Enquiry> findByEnquiryIdAndDeletionIndicator(String enquiryId, long l);
}