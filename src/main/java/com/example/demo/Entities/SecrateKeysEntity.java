package com.example.demo.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "secret_keys")
public class SecrateKeysEntity extends BaseEntity {

    @Column(name = "key_name", nullable = false, unique = true)
    private String keyName;

    @Column(name = "key_value", nullable = false)
    private String keyValue;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
}
