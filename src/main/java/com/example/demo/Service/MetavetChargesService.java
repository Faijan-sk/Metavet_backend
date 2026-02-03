package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.demo.Dto.MetavetChargesRequestDto;
import com.example.demo.Entities.MetavetCharges;
import com.example.demo.Entities.MetavetCharges.FeesType;
import com.example.demo.Entities.MetavetCharges.UserType;
import com.example.demo.Repository.MetavetChargesRepo;

import jakarta.validation.ValidationException;

@Service
public class MetavetChargesService {

	
	@Autowired
	private MetavetChargesRepo metavetChargesRepository;
	
	public MetavetCharges createFees(MetavetChargesRequestDto dto) {
		
		
		 UserType userType;
	     FeesType feesType;
	     
	     try {
	    	 
	    	 userType = UserType.valueOf(dto.getUserType());
	    	 
	    	 feesType = FeesType.valueOf(dto.getFeesType());
	    	 
	     }catch (IllegalArgumentException e) {
			throw new ValidationException("Invalid fees Type or User Type");
			
		}
	        
		MetavetCharges existingCharge = metavetChargesRepository.findByUserType(userType);
		  
		if(existingCharge != null ) {
			throw new ValidationException("For this role fees already created");
		}
		
		if(dto.getFeesValue() ==null ) {
			throw new ValidationException("Fees values is Required");
		}
		
		MetavetCharges newCharge = new MetavetCharges();
		
		
		newCharge.setFeesType(feesType);
		
		newCharge.setFeesValue(dto.getFeesValue());
		
		newCharge.setUserType(userType);
			
		return metavetChargesRepository.save(newCharge);
	}
	
	
	
	
	public MetavetCharges updateFees(MetavetChargesRequestDto dto) {
		
		UserType userType ; 
		FeesType feesType ;
		
		try {
			userType = UserType.valueOf(dto.getUserType());
			feesType = FeesType.valueOf(dto.getFeesType());
		}catch(IllegalArgumentException e) {
			
		 throw new ValidationException("Invalid Fees or User Type");
		}
		
		MetavetCharges existingCharges = metavetChargesRepository.findByUserType(userType);
		
		if(existingCharges == null) {
			throw new ValidationException("Charges Not found for this role");
		}
		
		if(dto.getFeesValue()==null) {
			throw new ValidationException("Fees Value is required");
			
		}
		
		existingCharges.setFeesType(feesType);
		existingCharges.setUserType(userType);
		existingCharges.setFeesValue(dto.getFeesValue());
		
		return metavetChargesRepository.save(existingCharges);
	
		
	}
	
	
	
	
	
}
