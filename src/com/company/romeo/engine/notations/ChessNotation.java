package com.company.romeo.engine.notations;


import com.company.romeo.engine.figures.Piece;
import com.company.romeo.engine.movements.Movable;

public interface ChessNotation {
    String transform(Piece piece, Movable movement);
}
