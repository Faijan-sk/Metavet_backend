package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Entities.PetsEntity;
import com.example.demo.Service.PetsService;

import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetController {
    
    @Autowired
    private PetsService petService;
    
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPet(@Valid @RequestBody PetsEntity pet, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check for validation errors
            if (result.hasErrors()) {
                response.put("status", "error");
                response.put("message", "Validation failed");
                response.put("errors", result.getAllErrors());
                return ResponseEntity.badRequest().body(response);
            }
            
            PetsEntity createdPet = petService.createPet(pet);
            
            response.put("status", "success");
            response.put("message", "Pet created successfully");
            response.put("data", createdPet);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    

    @DeleteMapping("/delete/{petUid}")
    public ResponseEntity<Map<String, Object>> deletePetByUid(@PathVariable UUID petUid) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = petService.deletePetByUid(petUid);
            
            if (deleted) {
                response.put("status", "success");
                response.put("message", "Pet deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to delete pet");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PutMapping("/update/{petUid}")
    public ResponseEntity<Map<String, Object>> updatePetByUid(
            @PathVariable UUID petUid, 
            @Valid @RequestBody PetsEntity updatedPet, 
            BindingResult result) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check for validation errors
            if (result.hasErrors()) {
                response.put("status", "error");
                response.put("message", "Validation failed");
                response.put("errors", result.getAllErrors());
                return ResponseEntity.badRequest().body(response);
            }
            
            PetsEntity pet = petService.updatePetByUid(petUid, updatedPet);
            
            response.put("status", "success");
            response.put("message", "Pet updated successfully");
            response.put("data", pet);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPets() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PetsEntity> pets = petService.getAllPets();
            
            response.put("status", "success");
            response.put("message", "Pets retrieved successfully");
            response.put("data", pets);
            response.put("count", pets.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
  
    @GetMapping("/{petId}")
    public ResponseEntity<Map<String, Object>> getPetById(@PathVariable Long petId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<PetsEntity> pet = petService.getPetById(petId);
            
            if (pet.isPresent()) {
                response.put("status", "success");
                response.put("message", "Pet found");
                response.put("data", pet.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Pet not found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
   
    @GetMapping("/owner")
    public ResponseEntity<Map<String, Object>> getPetsByOwner() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PetsEntity> pets = petService.getPetsByOwnerId();
            response.put("status", "success");
            response.put("message", "Pets retrieved successfully");
            response.put("data", pets);
            response.put("count", pets.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    

}