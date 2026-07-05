package com.learnrank.common.exception;

public class ValidationException extends RuntimeException {
    private final String field;
    public ValidationException(String field, String issue) {
        super(issue);
        this.field = field;
    }
    public String getField() { return field; }
}
