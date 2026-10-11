package com.app.ussd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.app.ussd.model.UssdSession;


public interface UssdSessionRepository extends JpaRepository<UssdSession, Long> {
 
	Optional<UssdSession> findByUssdSessionId(long id); 
	
	public List<UssdSession> findByServiceId(long serviceId);
}
