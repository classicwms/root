package com.ustorage.api.master.repository;

import com.ustorage.api.master.model.sbu.Sbu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SbuRepository extends JpaRepository<Sbu, Long>{

	public List<Sbu> findAll();

	public Optional<Sbu> findByCodeAndDeletionIndicator(String sbuId, long l);
}