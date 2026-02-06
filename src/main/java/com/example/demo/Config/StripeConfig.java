package com.example.demo.Config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import com.stripe.Stripe;

@Configuration
public class StripeConfig {
 
    @PostConstruct
    public void init() {
        Stripe.apiKey = "REMOVED51REaGAAciZnsrOJJdkbymXf4RJazqpr39biTeNLzxqPU4dLBtvlIpEWjbausNQ5arNn9DckFLZcE0UuoR5dETAL600XbkOPGoq";
    }
}