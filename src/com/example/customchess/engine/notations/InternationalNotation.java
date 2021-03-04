package com.example.customchess.engine.notations;


import com.example.customchess.engine.figures.Piece;
import com.example.customchess.engine.movements.Movable;

public class InternationalNotation implements ChessNotation {

    @Override
    public String transform(Piece piece, Movable movement) {
        return piece.toString() + movement.toString();
    }
}
