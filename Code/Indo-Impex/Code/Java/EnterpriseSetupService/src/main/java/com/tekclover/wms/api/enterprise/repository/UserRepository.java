package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    public List<User> findAll();

    public Optional<User> findByEmail(String email);

    public Optional<User> findByUsername(String username);
}