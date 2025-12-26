package com.example.demo.Service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.ServiceProviderRequestDto;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Repository.ServiceProviderRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Dto.ApiResponse;

@Service
public class ServiceProviderService {
	
	@Autowired
	private ServiceProviderRepo serviceProviderRepo;
	
	@Autowired
	private UserRepo userRepository;
	
	
	public ApiResponse<?> createService(ServiceProviderRequestDto dto) {
	    try {
	        UUID userUid = UUID.fromString(dto.getUid());
	        Optional<UsersEntity> user = userRepository.findByUid(userUid);

	        if (user.isEmpty()) {
	            return ApiResponse.notFound("❌ UID not found — please send a valid user UID");
	        }

	        ServiceProvider serviceProvider = new ServiceProvider();
	        serviceProvider.setServiceType(dto.getServiceType());
	        serviceProvider.setOwner(user.get());
	        serviceProviderRepo.save(serviceProvider);

	        return ApiResponse.success("Service Provider Created Successfully", serviceProvider);

	    } catch (IllegalArgumentException e) {
	        return ApiResponse.error("❌ Invalid UID format — please send correct UUID", 400);

	    } catch (Exception e) {
	        return ApiResponse.serverError("❌ Unexpected server error occurred, please try again");
	    }
	}
	
	
	public ApiResponse<?> getServiceProviderByUid(String uuid) {
	    try {
	        UUID serviceUid = UUID.fromString(uuid);

	        ServiceProvider serviceProvider = serviceProviderRepo.findByUid(serviceUid);

	        if (serviceProvider == null) {
	            return ApiResponse.notFound("❌ Service Provider not found for UID: " + uuid);
	        }

	        return ApiResponse.success("Service Provider fetched successfully", serviceProvider);

	    } catch (IllegalArgumentException e) {
	        return ApiResponse.error("❌ Invalid UID format — please provide a valid UUID", 400);

	    } catch (Exception e) {
	        return ApiResponse.serverError("❌ Unexpected server error occurred");
	    }
	}
}
