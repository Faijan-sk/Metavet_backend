package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Dto.ServiceProviderRequestDto;
import com.example.demo.Dto.ApiResponse;
import com.example.demo.Service.ServiceProviderService;

@RestController
@RequestMapping("/service-provider")
public class ServiceProviderController {
	
	@Autowired
	ServiceProviderService serviceProviderService;
	
	
	
	
	@GetMapping("/byUid/{uid}")
	public ApiResponse<?> getServiceProviderByUid(@PathVariable String uid) {
	    return serviceProviderService.getServiceProviderByUid(uid);
	}
}
