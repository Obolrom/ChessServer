package com.example.customchess.engine.automata;

import com.example.customchess.engine.exceptions.InvalidOrderMoveException;
import com.example.customchess.engine.figures.ChessPiece;
import com.example.customchess.engine.misc.Color;

import java.io.Serializable;

public interface Player extends Serializable {
    boolean isCorrectPlayerMove(ChessPiece selected) throws InvalidOrderMoveException;
    void changePlayer();
    Color getColor();
}
