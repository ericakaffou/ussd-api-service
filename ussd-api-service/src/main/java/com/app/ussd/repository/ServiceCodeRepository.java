package com.app.ussd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.app.ussd.model.ServiceCode;



public interface ServiceCodeRepository extends JpaRepository<ServiceCode, Long> {

	public Optional<ServiceCode> findByServiceCode(long sc);
}



