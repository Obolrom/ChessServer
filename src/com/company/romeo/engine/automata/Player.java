package com.company.romeo.engine.automata;


import com.company.romeo.engine.exceptions.InvalidOrderMoveException;
import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.figures.ChessPiece;

public interface Player {

    boolean isCorrectPlayerMove(ChessPiece selected) throws InvalidOrderMoveException;
    void changePlayer();
    Color getColor();
}
