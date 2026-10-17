/*


*/

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
import com.app.ussd.repository.SessionParamRepository;
import com.app.ussd.repository.UssdSessionRepository;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@Service
public class MeunUssdService {
	
	   
	    private final MenuOptionRepository optionRepo;	    
	    private final MenuRepository menuRepo;
	    private final SessionParamRepository sessionParamRepo;
		private final UssdSessionRepository ussdRepo;
		
		
	
	
    public MeunUssdService(MenuOptionRepository optionRepo, MenuRepository menuRepo,
				SessionParamRepository sessionParamRepo, UssdSessionRepository ussdRepo) {
			this.optionRepo = optionRepo;
			this.menuRepo = menuRepo;
			this.sessionParamRepo = sessionParamRepo;
			this.ussdRepo = ussdRepo;
		}


	public String getMenu(long menuLevel, UssdSession session ) { 
    	
    	String menuText ="";
    	Optional<Menu> optionalMenu = menuRepo.findById(menuLevel);
    	if(optionalMenu.isEmpty())
	 	{
    		menuText = menuText + "Menu Not Found " +'\n';
    		return menuText;
	 	}
    	Menu menu = optionalMenu.get();
    	
    	 switch(menu.getMenuType()){
    	
    	 case 0 :  // option menu   		   
    		       menuText = menu.getText() +'\n';
		    	    // ici verifier si le menu n'a pas d'option et si il y a des parametres	
		    	   int position = 0;
		           List<MenuOption> listOption = optionRepo.findByMenuId(session.getCurrentMenuLevel());
		           
		           //verifie si le menu contient des options sinon => menu de saisie de donnée
		           if(listOption != null) {
				         //get NextMenu position number
				           for(MenuOption option : listOption) {
				        		 if(option.isNextOption()) { 
				        			 position = option.getPostionNumber();			        			 
				        		 }
				        	 }          	   
					    	
					    	for(MenuOption option : listOption) {
					    		if(option.getPostionNumber() < position+1) {
					    		// faire un trie ici en fonction de postionNumber
					    		//menu = menu + tem.getPostionNumber()+":"+ tem.getResponse()+'\n';
					    		
					    	    menuText = menuText + option.getLabel()+'\n';	
					    		}
					    	}
					    	
		           }
		           break;
		           
    	 case 1 :  // Data input menu
    		       menuText = menu.getText() +'\n';
    		       break;
    	
    	 case 2 :  // Menu response
    		 
		    	   // Menu de reponse ( pour afficher le retour de requete vers serviceUrl de Option )
		   		   String url =  menu.getServiceUrl();			        		  
				   //  String url = option.getServiceUrl();
				   List<UssdSessionParam> params = sessionParamRepo.findByUssdSession(session);
				   String paramJson ="";
				   if(params != null) {
				        //put params in json format for post request
						for(UssdSessionParam param : params) {
							 paramJson =  "\"msisdn\":\""+session.getUssdSessionMsisdn()+"\","+
						     "\n\""+param.getParamName()+"\""+":"+"\""+param.getParamValue()+"\" \n"; 
						}
				   }else {
						paramJson =  "\"msisdn\":\""+session.getUssdSessionMsisdn()+"\" \n";
						}
					  
					    /* Call RestApi Service from option url with MSISDN  + or  parameters   */
						HttpResponse<String> ApiServiceResponse = Unirest.post(url)
								  .header("Content-Type", "application/json")
								  .body("{\r\n  "+paramJson+"  }")
								  .asObject(String.class);
						
						menuText = menu.getText() +'\n' + ApiServiceResponse ;
						
				   break;
						
        
    		   
    	 default :
	        	  break;
	               
    	} // Fin case
		   
    	
    	return menuText ;

    }
    
    
    public String getMenuNextPart(long  menuLevel, int nextPosition) { 
    	String menuText ="";	    	
    	
    	Menu menu = menuRepo.findById(menuLevel).get();
    	
    	menuText = menu.getText() +'\n';
    	// ici verifier si le menu n'a pas d'option et si il y a des parametres	
    	
       List<MenuOption> listOption = optionRepo.findByMenuId(menu.getId());	      	    	
       if(!listOption.isEmpty()) {	    	   
    	
    	for(MenuOption option : listOption) {	
    		    
    		if(option.getPostionNumber() > nextPosition) {	
    		// faire un trie ici en fonction de postionNumber
    		//menu = menu + tem.getPostionNumber()+":"+ tem.getResponse()+'\n';
    			menuText = menuText + option.getLabel()+'\n';
    		}
    			
    	  }	    		    	
    	
    	}menuText = menuText + "00: Home";

    	
    	
    			
    	return menuText ;
    	//return formatage.ResPullToXML(formatage.setResponse(request, menu, true));
    }
    

    public String getMenuError(long  menuLevel , String errorMsg) { 
    	
    	Menu menu = menuRepo.findById(menuLevel).get();
    	 String menuText ="";
    	 menuText = menu.getText() +'\n' + "Bad Request"+'\n' + errorMsg  + "00: Home";  	
    	 return menuText ;
    	//return formatage.ResPullToXML(formatage.setResponse(request, menu, true));
    }
    
    // partNb is indice of user input
    // seulement pour les anciennes session (bad) ! toutefois on peut s'en inspirer pour une methode qui va gérér tous les cas de partnb
    // à refaire totalement pour les cas mutlipart ex *100*2*6# , ces cas ne peuvent qu'etre que de nouvelles sessions
    // en outre cette fonction n'est pas utilisée dans le code	
    public String getSelectedOptionMenu(String[] part ,Long menuLevel , int partNb, Long sessionId) {
    	String response = null ;
    	//Check if session Existe on DB with interne Id	    
		 Optional<UssdSession> optionalSession  = ussdRepo.findById(sessionId);
		 if(!optionalSession.isPresent()) {
			 return null;
		 }
		 
		 UssdSession session = optionalSession.get();
    	
    	for(int i = partNb; i<=part.length-1 ; i++) {
    		// get all menuOption from  menu
        	List<MenuOption> listOption = optionRepo.findByMenuId(menuLevel);
        	//verifie si le menu contient des options sinon => menu de saisie de donnée
 			 if(listOption != null) {				 			 
	 				 for(MenuOption option : listOption) {
			 					 if(part[i].matches(option.getPostionNumber().toString())) {
			 						 
			 											 						  
				 						  //if Next Menu option selected
				 						  if (option.isNextOption()) {
				 							  int position = option.getPostionNumber();
				 							  response = this.getMenuNextPart(option.getMenu().getId(),position);
				 							  session.setCurrentMenuLevel(option.getMenu().getId());	
				 							  
				 							  ussdRepo.save(session);						  
				 							 
				 							  return response ;
				 							  
				 						  }else {
				 							 
				 							 // if not next menu option selected
				 							  i = i + 1;
				 							  response = this.getSelectedOptionMenu(part,option.getMenu().getId(),i,session.getId());
				 							  session.setCurrentMenuLevel(option.getMenu().getId());						  
				 							  ussdRepo.save(session);						  
				 							  
				 							  return response ;
				 							  }
			 						 
			 					      
			 		 		   }
 			        }
    		
	    	  }
 			  // ici verifier si le menu contient un parametre
 			  // si oui la valeur de part[i] sera associer à ce paramtre
 			 
	    	
	    	
	    }


    	 return null;
  }

}
