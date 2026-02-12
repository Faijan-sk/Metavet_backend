package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.SecrateKeysEntity;



@Repository
public interface SecrateKeysRepo extends JpaRepository<SecrateKeysEntity, Long> {

    Optional<SecrateKeysEntity> findByKeyName(String keyName);
}
