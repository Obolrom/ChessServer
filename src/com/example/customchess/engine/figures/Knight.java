package com.example.customchess.engine.figures;


import com.example.customchess.engine.Board;
import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.movements.Movable;
import com.example.customchess.engine.movements.MovementHistory;
import com.example.customchess.engine.movements.Position;
import com.example.customchess.engine.EndGameChecker;
import com.example.customchess.engine.exceptions.*;

public class Knight extends ChessPiece {

    public Knight(Color color, Position position) {
        super(position, 3.0, color);
    }

    @Override
    public String toString() {
        return "N";
    }

    @Override
    public boolean isTrajectoryValid(Movable movement) throws ChessException {
        Position start = movement.getStart();
        Position destination = movement.getDestination();

        int startVertical = start.getVertical().ordinal();
        int startHorizontal = start.getHorizontal() - 1;
        int destVertical = destination.getVertical().ordinal();
        int destHorizontal = destination.getHorizontal() - 1;

        if ((Math.abs(startHorizontal - destHorizontal) == 2 && Math.abs(startVertical - destVertical) == 1)
                || (Math.abs(startHorizontal - destHorizontal) == 1 && Math.abs(startVertical - destVertical) == 2)) {
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
