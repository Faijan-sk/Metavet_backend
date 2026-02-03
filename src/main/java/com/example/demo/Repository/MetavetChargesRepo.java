package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.MetavetCharges;
import com.example.demo.Entities.MetavetCharges.UserType;

@Repository
public interface MetavetChargesRepo extends JpaRepository<MetavetCharges, Long> {
	
	
	MetavetCharges findByUserType(UserType userType);

}
