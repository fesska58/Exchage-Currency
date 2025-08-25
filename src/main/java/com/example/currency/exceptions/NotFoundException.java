package com.example.currency.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String m){
        super(m);
    }
}
