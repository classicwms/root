package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.itemtype.ItemType;

@Repository
public interface ItemTypeRepository extends JpaRepository<ItemType, Long>{

	public List<ItemType> findAll();

	public Optional<ItemType> findByCodeAndDeletionIndicator(String itemTypeId, long l);
}