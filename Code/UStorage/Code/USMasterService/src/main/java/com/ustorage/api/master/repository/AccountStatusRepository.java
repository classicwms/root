package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.accountstatus.AccountStatus;

@Repository
public interface AccountStatusRepository extends JpaRepository<AccountStatus, Long>{

	public List<AccountStatus> findAll();

	public Optional<AccountStatus> findByCodeAndDeletionIndicator(String accountStatusId, long l);
}