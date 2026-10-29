package com.app.ussd.model.app;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor

public class UssdService extends AbstractEntity {	
	
	
	private String serviceName;	
	
    // Relation  Service to account 	
	@ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
	
	/* 
	
	 NEW idee : remaitre l'url de transfer ici car le formatage des datas de chaque mno sera fait dans son app3 */
	
	private boolean forwarded;
	
	private boolean enabled ;	
	
	//Relation with Menu 
    @OneToMany(mappedBy = "service") 
	private List<Menu> listMenu;    
  
    @OneToMany(mappedBy = "service") 
  	private List<UssdSession> ussdSession;
    
    //Relation with Covrage 
    @OneToMany(mappedBy = "service") 
	private List<Covrage> listCovrage;    
 
	
	
	
	

}
