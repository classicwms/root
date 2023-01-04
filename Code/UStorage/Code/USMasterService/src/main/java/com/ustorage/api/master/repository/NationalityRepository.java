package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.nationality.Nationality;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, Long>{

	public List<Nationality> findAll();

	public Optional<Nationality> findByCodeAndDeletionIndicator(String nationalityId, long l);
}