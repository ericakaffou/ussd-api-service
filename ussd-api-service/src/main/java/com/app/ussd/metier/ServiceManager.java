/* Ce service est le service d'entrée de app2. 
C'est ici que le service concerné est indentifié  : ?????? pb ??? : bah non on ne devrait pas identifier le service dans app2
car app2 est concu pour un service unique! donc l'identification devrait se faire ailleur !!!
idée : ce service doit etre deplacer dans app3 
Il utilisera l'url du service pour transfer app2Data vers app2 */

package com.app.ussd.metier;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.app.ussd.model.Menu;
import com.app.ussd.model.MenuOption;
import com.app.ussd.model.UssdSession;
import com.app.ussd.model.UssdSessionParam;
import com.app.ussd.repository.MenuOptionRepository;
import com.app.ussd.repository.MenuRepository;
import com.app.ussd.repository.UssdSessionParamRepository;
import com.app.ussd.repository.UssdSessionRepository;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceManager {
	
	private final MenuOptionRepository optionRepo;	
	private final MenuRepository menuRepo;	
	private final UssdSessionParamRepository sessionParamRepo;	
	private final UssdSessionRepository ussdRepo;	
	private final MeunUssdService metier ;	


	public ServiceManager(MenuOptionRepository optionRepo, MenuRepository menuRepo,
			UssdSessionParamRepository sessionParamRepo, UssdSessionRepository ussdRepo, MeunUssdService metier) {
		this.optionRepo = optionRepo;
		this.menuRepo = menuRepo;
		this.sessionParamRepo = sessionParamRepo;
		this.ussdRepo = ussdRepo;
		this.metier = metier;
	}

	public String ussdService( App2Data app2data ) {		
		
		//log.info("App2 - InboundRequestConroller");		
				
		 // new session
		if (app2data.isNewSession()) { 		
		// ici *100* est vu comme 2 parties        
		   return this.NewRequest(app2data.getParts(), app2data.getSession());
		
	    }else { //if (!request.isNewSession())	
		
		   return this.OldRequest(app2data.getInput(),  app2data.getSession());
	     }
		
		
	}
	
	/*
	 ----------------------------------------------------------------------------------------------------------------------------------------------------
	   */
	
	public String NewRequest( String[] parts ,UssdSession session) {
		 String response = null ;
		 switch(parts.length){
		   // 
	        case 1: 
	        	          
	        //	response = ussdSessionService.getMenu(1);
	        //	session.setCurrentMenuLevel(1);
	        //    ussdRepo.save(session); 				  
	            break;	       
	            
	        case 2:
	        	//c'est le part1 qui est le serviceCode , c'est avec lui qu'on identifiera le service , puis le menu principal
	        	// recuperer toutes les options du 1er menu(menu principal) et verifier si il  une optionNext est à true
	        	
	        	// recuperer toutes les options du 1er menu(menu principal) // Ici TODO : //à modifier pour recuperer l'id du main menu du service concerné		          
		        response = metier.getMenu(1,session);
		       	session.setCurrentMenuLevel(1L);
		        ussdRepo.save(session);
	        	break;     
	            
	        case 3:
	        	// get all menuOption from main menu
	        	List<MenuOption> listOption = optionRepo.findByMenuId(1L); // Ici TODO ://à modifier pour recuperer l'id du main menu du service concerné	
	        	//verifie si le menu contient des options sinon => menu de saisie de donnée
	 			 if(listOption != null) {				 			 
		 				 for(MenuOption option : listOption) {
		 					
		 				    //cheick if customer input match with option number
		 				    if(parts[2].matches(option.getPostionNumber().toString())) {			    	
		 						  
			 						  //if Next Menu option selected
			 						  if (option.isNextOption()) {
			 							  int position = option.getPostionNumber();
			 							  response = metier.getMenuNextPart(option.getMenu().getId(),position);
			 							  //session.setCurrentMenuLevel(option.getNextMenuLevel());	// not need 	 				  
			 							  ussdRepo.save(session);
			 						//	  return  ResponseEntity.ok(response) ;
			 							  
			 						 }else {
			 							 // if not next menu option selected
			 							  response = metier.getMenu(option.getNextMenuId(),session);
			 							  session.setCurrentMenuLevel(option.getNextMenuId());						  
			 							  ussdRepo.save(session);	
			 						//	  return  ResponseEntity.ok(response) ;
			 						  }
		 					 // if option is not submenu
		 					 // ici dans le else , recuperer l'url pour lance l'apirest service selectionné		 					  
		 				     }	 				     
			 									 				     
		 				     else {/*  getMenuErrorbad response*/
		 				    	 response = metier.getMenuError(option.getMenu().getId(),"No Option matched");
		 				     } 
		 				 }		 				 
	 			 }
	        	
	            break;
	            
	        default:
	        	response = null;
	            break;
	     }
		
		return response;
	}
	
       /*
	 ----------------------------------------------------------------------------------------------------------------------------------------------------
	   */
	
	public String OldRequest( String inputData ,UssdSession session) {
		 String response = null ;
		//old session
		 //Get Customer current MenuLevel and all option number 
			// if user input is 00 to home 
		 // Ici TODO : recuperer l'id du menu principal du service en lieu et place de 1L
		if(inputData.matches("00")){			    	  
	    	  // Home Menu
    	   	  response = metier.getMenu(1,session);
	    	  session.setCurrentMenuLevel(1L);
	          ussdRepo.save(session);
	          return response ;
              }		
 		  Long currentMenuLevel = session.getCurrentMenuLevel();
 		  
 		  //recuperer le menu ici 
 		  Menu currentMenu = menuRepo.findById(currentMenuLevel).get(); 		  
 		  switch (currentMenu.getMenuType()) {
 		  
 		  case 0 : 
 			      response = this.MenuOption(inputData, session);
 			      break; 
 			      
 		  case 1 : 
 			 response = this.MenuDataInput(inputData, session);
		      break;	
		      
 		 case 2 : 
 			 response = this.MenuServiceResponse(inputData, session);
		      break;	
		      
 		 default :
        	  break;
 		  
 		  }
	
		return response;
	}
	
	 /*
	 ----------------------------------------------------------------------------------------------------------------------------------------------------
	   */
	     public String MenuOption ( String inputData ,UssdSession session) {
	    	 String response = null ;
	    	 List<MenuOption> listOption = optionRepo.findByMenuId(session.getCurrentMenuLevel());
 			 //verifie si le menu contient des options sinon => menu de saisie de donnée
 			 if(listOption.isEmpty()) {
 				response = metier.getMenuError(session.getCurrentMenuLevel(), "No Option In Menu"); 
 			 }
 			  for(MenuOption option : listOption) { 
 				  
 				    //cheick if customer input match with option number
 				    if(inputData.matches(option.getPostionNumber().toString())) {    					    	
		 				    	//if matched , check if submenu				 					 
		 						  
		 						  //if Next Menu option selected
		 						  if (option.isNextOption()) {
		 							  int position = option.getPostionNumber();
		 							  response = metier.getMenuNextPart(session.getCurrentMenuLevel(),position);
		 							  //session.setCurrentMenuLevel(option.getNextMenuLevel());	// not need 	 				  
		 							  ussdRepo.save(session);						  
		 							  
		 						 }else {
		 							 // if not next menu option selected
		 							  response = metier.getMenu(option.getNextMenuId(),session);
		 							  session.setCurrentMenuLevel(option.getNextMenuId());						  
		 							  ussdRepo.save(session);						  
		 							 
		 						 }
 				     }
 				    
		 	 			 else {/*  getMenuErrorbad response*/
 				    	 response = metier.getMenuError(session.getCurrentMenuLevel(), "No Option matched");
 				         }
 				    
 				    }
	    	 
	     
 			return response; 
	    }
     /*
	 ----------------------------------------------------------------------------------------------------------------------------------------------------
	   */     
	     public String MenuDataInput ( String inputData ,UssdSession session) {
	    	 String response = null ;
	    	 
	    	// menu de saisie de donnée
	    	 	// ???? une fois la donnée saisie la suite ????? 		    	  
	    	   Optional<Menu> optinonalMenu = menuRepo.findById(session.getCurrentMenuLevel());
	    	   if(optinonalMenu.isPresent()) {
	    		   response = metier.getMenu(optinonalMenu.get().getNextMenuId(),session);
	    		   session.setCurrentMenuLevel(optinonalMenu.get().getNextMenuId()); 
	    		   ussdRepo.save(session);			    	   			    	   		    	  
	      
		    	 UssdSessionParam data = new UssdSessionParam();
		    	 data.setUssdSession(session);			    	 
		    	 data.setParamName(optinonalMenu.get().getParamName());
		    	 data.setParamValue(inputData);
		    	 // data to save
		    	 sessionParamRepo.save(data);				    	 
	    	 
	    	   } 
	    	 return response;
	     }
     /*
	 ----------------------------------------------------------------------------------------------------------------------------------------------------
	   */	     
	     public String MenuServiceResponse ( String inputData ,UssdSession session) {
	    	 String response = null ;
	    	 Menu menu = menuRepo.findById(session.getCurrentMenuLevel()).get();
	    	 String url = menu.getServiceUrl();
			  List<UssdSessionParam> params = sessionParamRepo.findByUssdSession(session);
			  String paramJson ="";
			  if(params != null) {
						  //put params in json format for post request
						  for(UssdSessionParam param : params) {
							  paramJson =  "\"msisdn\":\""+session.getUssdSessionMsisdn()+"\","+
						  "\n\""+param.getParamName()+"\""+":"+"\""+param.getParamValue()+"\" \n"; 
							  
						  }
			  }else {paramJson =  "\"msisdn\":\""+session.getUssdSessionMsisdn()+"\" \n";}
				  /* Call RestApi Service from option url with MSISDN  + or  parameters   */
				  
				  HttpResponse<String> responseCleintService = Unirest.post(url)
						  .header("Content-Type", "application/json")
						  .body("{\r\n  "+paramJson+"  }")
						  .asObject(String.class);
				  response = responseCleintService.getBody();
				 
	    	 return response;
	     }
	     
	 /*
	 ----------------------------------------------------------------------------------------------------------------------------------------------------
	   */	
}                          // endf class
