package com.app.ussd.model.dto;

import java.util.List;

import com.app.ussd.model.app.Covrage;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceCodeDto {
	
	private Long id;
	
	private Long serviceCode;	
	
	private boolean dedicated ;	// ture : dedicated ; false : shared		
	
	@JsonIgnore
	private List<Covrage> covrage;  
	
	


}
