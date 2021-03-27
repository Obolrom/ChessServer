package com.example.customchess.engine.figures;

import com.example.customchess.engine.Board;
import com.example.customchess.engine.EndGameChecker;
import com.example.customchess.engine.movements.MovementHistory;
import com.example.customchess.engine.exceptions.ChessException;
import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.movements.Movable;
import com.example.customchess.engine.movements.Position;


public interface Piece {
    void tryToMove(Movable movement, Board board, EndGameChecker gameAnalyser, MovementHistory lastMovement) throws ChessException;
    boolean isTrajectoryValid(Movable movement) throws ChessException;
    boolean isFightTrajectoryValid(Movable movement) throws ChessException;
    void move();
    Color    getColor();
}
