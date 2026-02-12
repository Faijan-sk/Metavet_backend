package com.example.demo.Config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import com.google.api.client.util.Value;
import com.stripe.Stripe;

@Configuration
public class StripeConfig {
 
	
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    
    
    
    @PostConstruct
    public void init() {
    	
        Stripe.apiKey = stripeSecretKey;
//        		"REMOVED51R72gCEZRh4nTXfc0CKNl8J1wH7DediKy9B571eiu3pZxZlJxj6lTU75DHbrFhzIOv0rHJePUH0IRJuCvykYwFhC00VFngZgdk";
        
    }
    
}

