package com.tekclover.wms.api.enterprise.repository;

import com.tekclover.wms.api.enterprise.model.doors.Doors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoorsRepository extends JpaRepository<Doors, Long>, JpaSpecificationExecutor<Doors> {

    public List<Doors> findAll();

    public Optional<Doors> findByDoorId(String doorId);
}