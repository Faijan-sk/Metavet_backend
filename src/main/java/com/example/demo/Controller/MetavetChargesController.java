package com.example.demo.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Config.SpringSecurityAuditorAware;
import com.example.demo.Dto.ApiResponse;
import com.example.demo.Dto.MetavetChargesRequestDto;
import com.example.demo.Entities.MetavetCharges;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Service.MetavetChargesService;

import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/metavet-charge")
public class MetavetChargesController {

    @Autowired
    private MetavetChargesService metavetChargesService;
    
   
    
    

    @PostMapping
    public ResponseEntity<ApiResponse<MetavetCharges>> createCharges(
            @RequestBody MetavetChargesRequestDto dto) {
    	
    	
    	
    	
        try {
            MetavetCharges response = metavetChargesService.createFees(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Charges created successfully", response));

        } catch (ValidationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Something went wrong"));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<MetavetCharges>> updateRoleFees(
            @RequestBody MetavetChargesRequestDto dto) {
    	
  

        try {
            MetavetCharges updatedCharge = metavetChargesService.updateFees(dto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success("Charges updated successfully", updatedCharge));

        } catch (ValidationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage(), 400));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("Something went wrong"));
        }
    }
    
    
    
}
