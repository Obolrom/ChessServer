package com.example.customchess.engine.exceptions;

public class CheckMateException extends CheckKingException {

    public CheckMateException() { }

    public CheckMateException(String message) {
        super(message);
    }
}
