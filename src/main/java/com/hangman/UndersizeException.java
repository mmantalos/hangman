package com.hangman;

public class UndersizeException extends Exception {
    public UndersizeException(String errorMessage) {
        super(errorMessage);
    }
}

