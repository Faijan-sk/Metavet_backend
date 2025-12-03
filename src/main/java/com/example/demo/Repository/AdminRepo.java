package com.example.demo.Repository;

import com.example.demo.Entities.AdminsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AdminRepo extends JpaRepository<AdminsEntity, Long> {

    Optional<AdminsEntity> findByUsername(String username);

    Optional<AdminsEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<AdminsEntity> findByRole(Integer role);

    List<AdminsEntity> findByRoleIn(List<Integer> roles);
}
