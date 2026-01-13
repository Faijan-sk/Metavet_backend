package com.example.demo.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Dto.ApiResponse;
import com.example.demo.Service.LoginAuthService;

@RestController
@RequestMapping("/api/auth")
public class LoginAuthController {

    @Autowired
    private LoginAuthService loginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyNumber(
            @RequestBody Map<String, String> request) {
        try {

            String phoneNumber = request.get("phone_number");

            ApiResponse<Map<String, Object>> response =
                    loginService.checkUser(phoneNumber);

            return ResponseEntity
                    .status(response.getStatusCode() != null
                            ? response.getStatusCode()
                            : 200)
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.serverError("Internal server error")
            );
        }
    }
}
