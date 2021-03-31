package com.example.customchess.engine.exceptions;

public class DrawException extends ChessException {
    public DrawException() { }

    public DrawException(String message) {
        super(message);
    }
}
