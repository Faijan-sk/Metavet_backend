package com.example.demo.Service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.ServiceProviderRequestDto;
import com.example.demo.Entities.BehaviouristKyc;
import com.example.demo.Entities.GroomerKyc;
import com.example.demo.Entities.ServiceProvider;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Entities.WalkerKyc;
import com.example.demo.Repository.BehaviouristKycRepo;
import com.example.demo.Repository.GroomerKycRepo;
import com.example.demo.Repository.ServiceProviderRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Repository.WalkerKycRepo;
import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Dto.ApiResponse;

@Service
public class ServiceProviderService {
	
	@Autowired
	private ServiceProviderRepo serviceProviderRepo;
	
	@Autowired
	private UserRepo userRepository;
	
	@Autowired
    private SpringSecurityAuditorAware auditorAware;
	
	@Autowired
	private GroomerKycRepo groomerKycRepository;
	
	@Autowired
	private WalkerKycRepo walkerKycRespository;
	
	@Autowired
	private BehaviouristKycRepo behaviouristKycRepository;
    
	
    
	
	
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
	
	
	public ApiResponse<?> getServiceProviderByUid(String uuid) {
	    try {
	        UUID serviceUid = UUID.fromString(uuid);

	        ServiceProvider serviceProvider = serviceProviderRepo.findByUid(serviceUid).get();

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
	
	// Add this method to ServiceProviderService
	public UUID getServiceProviderUidByUserUid(UUID userUid) {
	    ServiceProvider serviceProvider = serviceProviderRepo.findByOwner_Uid(userUid)
	            .orElseThrow(() -> new RuntimeException("Service Provider not found for user"));
	    return serviceProvider.getUid();
	}
	
	
	
	
	public ApiResponse<?> getServiceProviderStatus() {

    Optional<UsersEntity> userOpt = auditorAware.getCurrentAuditor();

    if (userOpt.isEmpty()) {
        return ApiResponse.unauthorized("User not found");
    }

    ServiceProvider serviceProvider =
            serviceProviderRepo.findByOwnerUid(userOpt.get().getUid());

    if (serviceProvider == null) {
        return ApiResponse.notFound("Service Provider not found");
    }

    ServiceProvider.ServiceType serviceType = serviceProvider.getServiceType();

    if (serviceType == null) {
        return ApiResponse.error("Service type not assigned", 400);
    }

    switch (serviceType) {

        case Pet_Walker: {

            Optional<WalkerKyc> kycOpt =
                    walkerKycRespository.findByServiceProviderUid(serviceProvider.getUid());

            if (kycOpt.isEmpty()) {
                return ApiResponse.statusResponse(
                        false,
                        "KYC not submitted",
                        "NOT_SUBMITTED",
                        400
                );
            }

            WalkerKyc.ApplicationStatus status = kycOpt.get().getStatus();

            return ApiResponse.statusResponse(
                    true,
                    "Walker KYC status fetched successfully",
                    status.name(),
                    200
            );
        }

        case Pet_Groomer: {

            Optional<GroomerKyc> kycOpt =
                    groomerKycRepository.findByServiceProviderUid(serviceProvider.getUid());

            if (kycOpt.isEmpty()) {
                return ApiResponse.statusResponse(
                        false,
                        "KYC not submitted",
                        "NOT_SUBMITTED",
                        400
                );
            }

            GroomerKyc.ApplicationStatus status = kycOpt.get().getStatus();

            return ApiResponse.statusResponse(
                    true,
                    "Groomer KYC status fetched successfully",
                    status.name(),
                    200
            );
        }

        case Pet_Behaviourist: {

            Optional<BehaviouristKyc> kycOpt =
                    behaviouristKycRepository.findByServiceProviderUid(serviceProvider.getUid());

            if (kycOpt.isEmpty()) {
                return ApiResponse.statusResponse(
                        false,
                        "KYC not submitted",
                        "NOT_SUBMITTED",
                        400
                );
            }

            BehaviouristKyc.ApprovalStatus status = kycOpt.get().getStatus();

            return ApiResponse.statusResponse(
                    true,
                    "Behaviourist KYC status fetched successfully",
                    status.name(),
                    200
            );
        }

        default:
            return ApiResponse.error("Invalid service type", 400);
    }
}

	
	
}
