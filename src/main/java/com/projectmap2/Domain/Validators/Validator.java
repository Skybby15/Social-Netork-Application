package com.projectmap2.Domain.Validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}