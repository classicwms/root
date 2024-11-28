package com.tekclover.wms.api.masters.repository;

import com.tekclover.wms.api.masters.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public List<User> findAll();

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}