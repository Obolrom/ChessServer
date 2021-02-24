package com.company.romeo.engine;

import com.company.romeo.engine.exceptions.CheckMateException;
import com.company.romeo.engine.exceptions.ChessException;
import com.company.romeo.engine.exceptions.DrawException;
import com.company.romeo.engine.movements.Movable;
import com.company.romeo.engine.automata.Player;

public interface Game {
    void checkForPat() throws DrawException;
    void checkForCheckMate() throws CheckMateException;
    void promotion(String choice);
    void tryToMakeMovement(Movable movement) throws ChessException;
    void setCurrentPlayer(Player game);
}
