package com.app.ussd.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuDto {
	
	private Long id; // Menu Level unique identifier     	
    private String text;		
    private Long nextMenuId;	// For validation Menu oo indicate response menu	
	private String paramName;	// 1: data Input Menu 
	private boolean mainMenu;   // 1 if Main Menu , 0 default
	private int menuType;   // 0: option menu ; 1: data Input Menu ; 2:Response Menu
    private String serviceUrl;  // Url of Third party restAPI service 	
	private boolean answerRequired;  	
	// Relation Menu to Service		
    private Long serviceId ;
	
   

}
