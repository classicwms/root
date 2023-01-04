package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.room.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>{

	public List<Room> findAll();

	public Optional<Room> findByCodeAndDeletionIndicator(String roomId, long l);
}