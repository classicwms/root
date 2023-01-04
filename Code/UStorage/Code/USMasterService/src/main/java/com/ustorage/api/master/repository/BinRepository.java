package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.bin.Bin;

@Repository
public interface BinRepository extends JpaRepository<Bin, Long>{

	public List<Bin> findAll();

	public Optional<Bin> findByCodeAndDeletionIndicator(String binId, long l);
}