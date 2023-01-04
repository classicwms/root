package com.ustorage.api.master.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ustorage.api.master.model.room.AddRoom;
import com.ustorage.api.master.model.room.Room;
import com.ustorage.api.master.model.room.UpdateRoom;
import com.ustorage.api.master.repository.RoomRepository;
import com.ustorage.api.master.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoomService {
	
	@Autowired
	private RoomRepository roomRepository;
	
	public List<Room> getRoom () {
		List<Room> roomList =  roomRepository.findAll();
		roomList = roomList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
		return roomList;
	}
	
	/**
	 * getRoom
	 * @param roomId
	 * @return
	 */
	public Room getRoom (String roomId) {
		Optional<Room> room = roomRepository.findByCodeAndDeletionIndicator(roomId, 0L);
		if (room.isEmpty()) {
			return null;
		}
		return room.get();
	}
	
	/**
	 * createRoom
	 * @param newRoom
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Room createRoom (AddRoom newRoom, String loginUserId) 
			throws IllegalAccessException, InvocationTargetException, Exception {
		Room dbRoom = new Room();
		BeanUtils.copyProperties(newRoom, dbRoom, CommonUtils.getNullPropertyNames(newRoom));
		dbRoom.setDeletionIndicator(0L);
		dbRoom.setCreatedBy(loginUserId);
		dbRoom.setUpdatedBy(loginUserId);
		dbRoom.setCreatedOn(new Date());
		dbRoom.setUpdatedOn(new Date());
		return roomRepository.save(dbRoom);
	}
	
	/**
	 * updateRoom
	 * @param roomId
	 * @param loginUserId 
	 * @param updateRoom
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Room updateRoom (String code, String loginUserId, UpdateRoom updateRoom)
			throws IllegalAccessException, InvocationTargetException {
		Room dbRoom = getRoom(code);
		BeanUtils.copyProperties(updateRoom, dbRoom, CommonUtils.getNullPropertyNames(updateRoom));
		dbRoom.setUpdatedBy(loginUserId);
		dbRoom.setUpdatedOn(new Date());
		return roomRepository.save(dbRoom);
	}
	
	/**
	 * deleteRoom
	 * @param loginUserID 
	 * @param roomCode
	 */
	public void deleteRoom (String roomModuleId, String loginUserID) {
		Room room = getRoom(roomModuleId);
		if (room != null) {
			room.setDeletionIndicator(1L);
			room.setUpdatedBy(loginUserID);
			room.setUpdatedOn(new Date());
			roomRepository.save(room);
		} else {
			throw new EntityNotFoundException("Error in deleting Id: " + roomModuleId);
		}
	}
}
