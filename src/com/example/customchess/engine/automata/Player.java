package com.example.customchess.engine.automata;


import com.example.customchess.engine.exceptions.InvalidOrderMoveException;
import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.figures.ChessPiece;

public interface Player {

    boolean isCorrectPlayerMove(ChessPiece selected) throws InvalidOrderMoveException;
    void changePlayer();
    Color getColor();
}
