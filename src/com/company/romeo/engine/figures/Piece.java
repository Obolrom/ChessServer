package com.company.romeo.engine.figures;


import com.company.romeo.engine.Board;
import com.company.romeo.engine.exceptions.ChessException;
import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.movements.Movable;
import com.company.romeo.engine.movements.MovementHistory;
import com.company.romeo.engine.movements.Position;
import com.company.romeo.engine.EndGameChecker;

public interface Piece {
    void tryToMove(Movable movement, Board board, EndGameChecker gameAnalyser, MovementHistory lastMovement) throws ChessException;
    boolean isTrajectoryValid(Movable movement) throws ChessException;
    boolean isFightTrajectoryValid(Movable movement) throws ChessException;
    void move();
    Position getCurrentPosition();
    Color getColor();
    void     setPosition(Position newPosition);
}
