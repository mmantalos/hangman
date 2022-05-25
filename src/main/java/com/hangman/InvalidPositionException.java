package com.hangman;

public class InvalidPositionException extends Exception {
    public InvalidPositionException(String errorMessage) {
        super(errorMessage);
    }
}

