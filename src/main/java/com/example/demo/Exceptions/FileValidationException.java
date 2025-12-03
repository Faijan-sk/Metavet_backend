package com.example.demo.Exceptions;


public class FileValidationException extends Exception {
    private String fieldName;
    private String errorMessage;

    public FileValidationException(String fieldName, String errorMessage) {
        super(errorMessage);
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}