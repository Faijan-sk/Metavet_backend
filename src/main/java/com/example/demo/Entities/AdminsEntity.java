package com.example.demo.Entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(
    name = "admin_entity",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
    }
)
public class AdminsEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Role as Integer (1 = Super Admin, 2 = Admin, 3 = Sub Admin)
    @Column(name = "role", nullable = false)
    private Integer role;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // ⚠️ IMPORTANT: ab yaha actual username return karenge, email nahi
    @Override
    public String getUsername() {
        // UserDetails ka getUsername() -> login identifier bana sakte ho
        // abhi ke liye hum username ko primary login ID maan rahe hain
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Agar kahin alag se email chahiye to ye use karo:
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ---------- UserDetails ke required methods ----------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role ko authority bana diya (number as string)
        return List.of(() -> String.valueOf(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return true; 
    }

    // Helper method -> role ka naam
    public String getRoleName() {
        return switch (role) {
            case 1 -> "Super Admin";
            case 2 -> "Admin";
            case 3 -> "Sub Admin";
            default -> "Unknown";
        };
    }
}
