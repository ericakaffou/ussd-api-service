package com.app.ussd.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.ussd.Constants;
import com.app.ussd.exception.EntityNotFoundException;
import com.app.ussd.exception.ErrorCodes;
import com.app.ussd.exception.Exception;
import com.app.ussd.exception.InvalidEntityException;
import com.app.ussd.model.ServiceCode;
import com.app.ussd.model.dto.ServiceCodeDto;
import com.app.ussd.repository.ServiceCodeRepository;
import com.app.ussd.validator.ServiceCodeValidator;

@Service
public class ServiceCodeService {

	private final ServiceCodeRepository scRepo;

	public ServiceCodeService(ServiceCodeRepository scRepo) {
		this.scRepo = scRepo;
	}

	//Save
	public ServiceCodeDto Save(ServiceCodeDto dto) {
		List<String> errors = ServiceCodeValidator.validate(dto);
		
		if(!errors.isEmpty()) {
			 throw new InvalidEntityException (Constants.ERROR,ErrorCodes.SERVICE_CODE_NOT_VALID, errors);
		}
		
		Optional<ServiceCode> optionalSC = scRepo.findByServiceCode(dto.getServiceCode());
		if (optionalSC.isPresent()) {
			throw new Exception(Constants.ERROR, ErrorCodes.SERVICE_CODE_ALREADY_EXIST);
		}
		
		 	ServiceCode serviceCode = new ServiceCode();
		 // serviceCode.setId(dto.getId());
		    serviceCode.setServiceCode(dto.getServiceCode());
		    serviceCode.setDedicated(dto.isDedicated());
			return  this.toDto(scRepo.save(serviceCode));
		    
	}
	
	//findById
	public ServiceCodeDto FindById (Long id) {
		if (id == null) {
			throw new Exception(Constants.ERROR,ErrorCodes.ID_NULL);			
		   }
		
		 Optional<ServiceCode> optionalSc = scRepo.findById(id);
		 if(optionalSc.isEmpty()) {
			 throw new EntityNotFoundException (Constants.FAILED,ErrorCodes.SERVICE_CODE_NOT_FOUND);
		 }
		
		 return 	this.toDto(optionalSc.get()); 
	}
	
	//findByServiceCode
		public ServiceCodeDto FindByServiceCode (Long serviceCode) {
			if (serviceCode == null) {
				throw new Exception(Constants.ERROR,ErrorCodes.SERVICE_CODE_NULL);			
			   }			
			 Optional<ServiceCode> optionalSc = scRepo.findByServiceCode(serviceCode);
			 if(optionalSc.isEmpty()) {
				 throw new EntityNotFoundException (Constants.FAILED,ErrorCodes.SERVICE_CODE_NOT_FOUND);
			 }
			
			 return 	this.toDto(optionalSc.get()); 
		}
	
	// ListAll
	public List<ServiceCodeDto> FindAll() {
		
		List<ServiceCode> listServiceCode = scRepo.findAll();
		List<ServiceCodeDto> listServiceCodeDto = new ArrayList<ServiceCodeDto>();
		
		if(listServiceCode.isEmpty()) {
			throw new EntityNotFoundException (Constants.FAILED,ErrorCodes.SERVICE_CODE_NOT_FOUND);			
		}			
		for( ServiceCode serviceCode : listServiceCode ) {
			listServiceCodeDto.add(this.toDto(serviceCode))  ;			
		}			
		return listServiceCodeDto;
	}
	

	//delete
	public void Delete(Long id) {
		
		//check and get Route		
		ServiceCodeDto serviceCode = this.FindById(id);				
		scRepo.deleteById(serviceCode.getId());
		
		
	}
	
	
	
	// Mapping de Account -> AccountDto
    public  ServiceCodeDto toDto(ServiceCode serviceCode) {
    	
    	if (serviceCode == null) {    		
    		return null ;
    		// TODO throw an execption
    	}
    	
    	return ServiceCodeDto.builder()
    			.id(serviceCode.getId())
    			.serviceCode(serviceCode.getServiceCode())
    			.dedicated(serviceCode.isDedicated())
    			.build() ;
    }
	
	
}
