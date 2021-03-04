package com.example.customchess.engine.notations;


import com.example.customchess.engine.figures.Piece;
import com.example.customchess.engine.movements.Movable;

public interface ChessNotation {
    String transform(Piece piece, Movable movement);
}
