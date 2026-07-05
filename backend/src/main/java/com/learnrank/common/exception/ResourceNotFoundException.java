package com.learnrank.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entityType, Object id) {
        super(entityType + " with id '" + id + "' was not found.");
    }
}
