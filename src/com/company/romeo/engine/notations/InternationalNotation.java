package com.company.romeo.engine.notations;


import com.company.romeo.engine.figures.Piece;
import com.company.romeo.engine.movements.Movable;

public class InternationalNotation implements ChessNotation {

    @Override
    public String transform(Piece piece, Movable movement) {
        return piece.toString() + movement.toString();
    }
}
