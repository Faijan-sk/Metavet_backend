package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Dto.ServiceProviderRequestDto;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Repository.ServiceProviderRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Dto.ApiResponse;
import com.example.demo.Service.ServiceProviderService;

@RestController
@RequestMapping("/service-provider")
public class ServiceProviderController {
	
	@Autowired
	ServiceProviderService serviceProviderService;
	
	@Autowired
	private ServiceProviderRepo serviceProviderRepo;
	
	@Autowired
	private UserRepo userRepository;
	
	@Autowired
    private SpringSecurityAuditorAware auditorAware;
	
	
	
	
	
	@GetMapping("/byUid/{uid}")
	public ApiResponse<?> getServiceProviderByUid(@PathVariable String uid) {
	    return serviceProviderService.getServiceProviderByUid(uid);
	}
	
	
	@PostMapping("/create")
public ApiResponse<?> createService(ServiceProviderRequestDto dto) {
		
		UsersEntity owner = auditorAware.getCurrentAuditor().orElse(null);

	    try {
	    	
	    	UUID userUid = owner.getUid();
	    	
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
	
	



}
