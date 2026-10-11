package com.app.ussd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.app.ussd.model.RouteMno;


public interface RouteMnoRepository extends JpaRepository<RouteMno, Long>{
	
	public List<RouteMno> findByCovrageId(Long id);
	
	public List<RouteMno> findByMnoId(Long id);
	
	public Optional<RouteMno> findByCovrageIdAndServiceIdAndMnoId(Long covrageId , Long serviceId , Long mnoId);
	
	
	

}
