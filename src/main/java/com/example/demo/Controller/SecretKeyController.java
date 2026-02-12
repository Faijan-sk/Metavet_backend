package com.example.demo.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Entities.SecrateKeysEntity;
import com.example.demo.Service.SecretKeyService;

@RestController
@RequestMapping("/api/keys")
public class SecretKeyController {

    private final SecretKeyService secretKeyService;

    public SecretKeyController(SecretKeyService secretKeyService) {
        this.secretKeyService = secretKeyService;
    }

    // 🔹 Get key value by key name
    @GetMapping("/{keyName}")
    public ResponseEntity<String> getKey(@PathVariable String keyName) {
        return ResponseEntity.ok(secretKeyService.getValueByKeyName(keyName));
    }

  
    @PostMapping
    public ResponseEntity<SecrateKeysEntity> saveKey(
            @RequestBody SecrateKeysEntity request) {

        return ResponseEntity.ok(
                secretKeyService.saveKey(
                        request.getKeyName(),
                        request.getKeyValue()
                )
        );
    }

}
