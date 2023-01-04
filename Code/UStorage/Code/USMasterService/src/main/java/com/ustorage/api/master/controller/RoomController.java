package com.ustorage.api.master.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ustorage.api.master.model.room.AddRoom;
import com.ustorage.api.master.model.room.Room;
import com.ustorage.api.master.model.room.UpdateRoom;
import com.ustorage.api.master.service.RoomService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Room" }, value = "Room Operations related to RoomController") 
@SwaggerDefinition(tags = { @Tag(name = "Room", description = "Operations related to Room") })
@RequestMapping("/room")
@RestController
public class RoomController {

	@Autowired
	RoomService roomService;

	@ApiOperation(response = Room.class, value = "Get all Room details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Room> roomList = roomService.getRoom();
		return new ResponseEntity<>(roomList, HttpStatus.OK);
	}

	@ApiOperation(response = Room.class, value = "Get a Room") // label for swagger
	@GetMapping("/{roomId}")
	public ResponseEntity<?> getRoom(@PathVariable String roomId) {
		Room dbRoom = roomService.getRoom(roomId);
		log.info("Room : " + dbRoom);
		return new ResponseEntity<>(dbRoom, HttpStatus.OK);
	}

	@ApiOperation(response = Room.class, value = "Create Room") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postRoom(@Valid @RequestBody AddRoom newRoom,
			@RequestParam String loginUserID) throws Exception {
		Room createdRoom = roomService.createRoom(newRoom, loginUserID);
		return new ResponseEntity<>(createdRoom, HttpStatus.OK);
	}

	@ApiOperation(response = Room.class, value = "Update Room") // label for swagger
	@PatchMapping("/{roomId}")
	public ResponseEntity<?> patchRoom(@PathVariable String roomId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateRoom updateRoom)
			throws IllegalAccessException, InvocationTargetException {
		Room updatedRoom = roomService.updateRoom(roomId, loginUserID,
				updateRoom);
		return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
	}

	@ApiOperation(response = Room.class, value = "Delete Room") // label for swagger
	@DeleteMapping("/{roomId}")
	public ResponseEntity<?> deleteRoom(@PathVariable String roomId, @RequestParam String loginUserID) {
		roomService.deleteRoom(roomId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
