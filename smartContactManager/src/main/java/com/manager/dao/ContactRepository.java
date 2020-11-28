package com.manager.dao;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.manager.entities.Contacts;



public interface ContactRepository extends JpaRepository<Contacts,Integer>{
	
	//current page
	//contact per page
	@Query("from Contacts as u where u.user.id=:user_id")
	public Page<Contacts>  findContactsByUser(@Param("user_id") int user_id,Pageable pageable);
}
