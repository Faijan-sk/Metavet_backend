package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Dto.ApiResponse;
import com.example.demo.Dto.ServiceProviderRequestDto;
import com.example.demo.Entities.UsersEntity;
import com.example.demo.Service.UserService;

@RestController
@Controller
@RequestMapping("/clients/")
public class CommonController {
	
	@Autowired
	private UserService userService;
	
	
	
	
	
	@GetMapping("/getAll")
	public ResponseEntity<ApiResponse<List<UsersEntity>>> getAllClient() {

	    List<UsersEntity> users = userService.getAllUsers();

	    // Empty list case
	    if (users == null || users.isEmpty()) {
	        return ResponseEntity
	                .status(HttpStatus.OK)
	                .body(ApiResponse.success("No users found", users));
	    }

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(ApiResponse.success("Users fetched successfully", users));
	}
	
	
	
	
	
	@GetMapping("/by-uid/{uid}")
    public ResponseEntity<ApiResponse<?>> getUserByUid(@PathVariable String uid) {
		
		UUID uuid = UUID.fromString(uid);


        Optional<UsersEntity> userOpt = userService.getUserByUuid(uuid);

        // ❌ User not found
        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(
                            "User not found",
                            404,
                            "NOT_FOUND"
                    ));
        }

        // ✅ User found
        return ResponseEntity.ok(
                ApiResponse.success(
                        "User fetched successfully",
                        userOpt.get()
                )
        );
    }
	
	@DeleteMapping("/delete-byuid/{uid}")
	public ResponseEntity<?> deleteClientByuid(@PathVariable String uid) {

	    UUID uuid;
	    try {
	        uuid = UUID.fromString(uid);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity
	                .badRequest()
	                .body("Invalid UUID format");
	    }
	    
	    // 🔁 Service response return karo
	    return userService.deleteClientByUid(uuid);
	}

	

}
