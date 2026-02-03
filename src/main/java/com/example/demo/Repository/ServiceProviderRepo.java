package com.example.demo.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.ServiceProvider;

@Repository
public interface ServiceProviderRepo extends JpaRepository<ServiceProvider, Long>{

	ServiceProvider findByOwnerUid(UUID uid);
	
//	ServiceProvider findByUid(UUID uid);
	
	// Add these methods to ServiceProviderRepo
	Optional<ServiceProvider> findByUid(UUID uid);
	boolean existsByUid(UUID uid);
	Optional<ServiceProvider> findByOwner_Uid(UUID ownerUid);

}
