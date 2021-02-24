package com.company.romeo.engine.figures;


import com.company.romeo.engine.Board;
import com.company.romeo.engine.exceptions.*;
import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.movements.Movable;
import com.company.romeo.engine.movements.MovementHistory;
import com.company.romeo.engine.movements.Position;
import com.company.romeo.engine.EndGameChecker;

public class Bishop extends ChessPiece {

    public Bishop(Color color, Position position) {
        super(position, 3.5, color);
    }

    @Override
    public boolean isTrajectoryValid(Movable movement) throws ChessException {
        Position start = movement.getStart();
        Position destination = movement.getDestination();

        int startVertical = start.getVertical().ordinal();
        int startHorizontal = start.getHorizontal() - 1;
        int destVertical = destination.getVertical().ordinal();
        int destHorizontal = destination.getHorizontal() - 1;

        int verticalDiff = Math.abs(startVertical - destVertical);
        int horizontalDiff = Math.abs(startHorizontal - destHorizontal);

        if (verticalDiff == horizontalDiff) {
            return true;
        }
        throw new InvalidMoveException("Invalid move\n" + start + " - " + destination);
    }

    @Override
    public boolean isFightTrajectoryValid(Movable movement) throws ChessException {
        return isTrajectoryValid(movement);
    }

    @Override
    public void move() {
        firstMove = false;
    }

    @Override
    public String toString() {
        return "B";
    }

    @Override
    public void tryToMove(Movable movement, Board board, EndGameChecker gameAnalyser, MovementHistory lastMovement) throws ChessException {
        ChessPiece startFigure = (ChessPiece) board.findBy(movement.getStart());
        ChessPiece destinationFigure = (ChessPiece) board.findBy(movement.getDestination());

        if (!board.isCageEmpty(destinationFigure) && startFigure.hasSameColor(destinationFigure)) {
            throw new OneTeamPiecesSelectedException("One team pieces are selected");

        } else if (board.isCageEmpty(destinationFigure)) {
            if (isTrajectoryValid(movement) & board.isDistanceFree(movement)) {
                throw new MoveOnEmptyCageException("default move");
            }
        } else {
            if (isFightTrajectoryValid(movement) & board.isDistanceFree(movement)) {
                throw new BeatFigureException("beat figure move");
            }
        }
        throw new InvalidMoveException("Invalid move\n" + movement.getStart() + " - " + movement.getDestination());
    }
}
