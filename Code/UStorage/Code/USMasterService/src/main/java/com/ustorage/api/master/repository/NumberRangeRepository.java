package com.ustorage.api.master.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.numberrange.NumberRange;

import javax.validation.constraints.NotNull;

@Repository
public interface NumberRangeRepository extends JpaRepository<NumberRange, Long> {
	
	List<NumberRange> findAll();
	Optional<NumberRange> findById(Long id);

	Optional<NumberRange> findByNumberRangeCode(Long numberRangeCode);
	

	Optional<NumberRange> findByNumberRangeCodeAndDescriptionAndDeletionIndicator(Long numberRangeCode, String description, long l);

	Optional<NumberRange> findByNumberRangeCodeAndDescription(Long numberRangeCode, String description);
}


