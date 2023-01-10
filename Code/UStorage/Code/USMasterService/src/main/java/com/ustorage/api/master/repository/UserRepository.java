package com.ustorage.api.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ustorage.api.master.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	public List<User> findAll();
	Optional<User> findByUsernameAndDeletionIndicator(String username, long l);
	Optional<User> findByIdAndDeletionIndicator(long id, long l);

	//userName
	@Query(value = "SELECT tu.username AS username \r\n"
			+ "FROM tbluser tu \r\n"
			+ "WHERE \n"
			+"(COALESCE(:username,null) IS NULL OR (tu.username IN (:username))) and \n"
			+ "tu.IS_DELETED = 0", nativeQuery = true)
	public String getUsrName (
			@Param(value = "username") String username);
}