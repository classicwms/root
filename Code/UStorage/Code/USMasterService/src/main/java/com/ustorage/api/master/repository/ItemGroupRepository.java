package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.itemgroup.ItemGroup;

@Repository
public interface ItemGroupRepository extends JpaRepository<ItemGroup, Long>{

	public List<ItemGroup> findAll();

	public Optional<ItemGroup> findByCodeAndDeletionIndicator(String itemGroupId, long l);
}