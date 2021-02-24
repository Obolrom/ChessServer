package com.company.romeo.engine.figures;

import com.company.romeo.engine.Board;
import com.company.romeo.engine.exceptions.*;
import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.movements.Movable;
import com.company.romeo.engine.movements.MovementHistory;
import com.company.romeo.engine.movements.Position;
import com.company.romeo.engine.EndGameChecker;

public class Pawn extends ChessPiece {

    public Pawn(Color color, Position position) {
        super(position, 1.0, color);
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean isTrajectoryValid(Movable movement) throws ChessException {
        Position start = movement.getStart();
        Position destination = movement.getDestination();

        int startVertical = start.getVertical().ordinal();
        int startHorizontal = start.getHorizontal() - 1;
        int destVertical = destination.getVertical().ordinal();
        int destHorizontal = destination.getHorizontal() - 1;
        int horizontalDiff = startHorizontal - destHorizontal;

        if (this.color.equals(Color.White)) {
            horizontalDiff = -horizontalDiff;
        }

        if (startVertical == destVertical & horizontalDiff > 0) {
            if (firstMove && horizontalDiff < 3) {
                return true;
            }
            if (horizontalDiff < 2) {
                return true;
            }
        }
        throw new InvalidMoveException("Invalid move\n" + start + " - " + destination);
    }

    @Override
    public boolean isFightTrajectoryValid(Movable movement) throws ChessException {
        Position start = movement.getStart();
        Position destination = movement.getDestination();

        int startVertical = start.getVertical().ordinal();
        int startHorizontal = start.getHorizontal() - 1;
        int destVertical = destination.getVertical().ordinal();
        int destHorizontal = destination.getHorizontal() - 1;
        int horizontalDiff = startHorizontal - destHorizontal;
        int verticalDiff = Math.abs(startVertical - destVertical);

        if (this.color.equals(Color.White)) {
            if (horizontalDiff == -1 & verticalDiff == 1) return true;

        } else if (horizontalDiff == 1 & verticalDiff == 1) {
            return true;
        }
        throw new InvalidMoveException("Invalid move\n" + start + " - " + destination);
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
            if (wasPawnEnPassant(lastMovement, movement)) {
                throw new PawnEnPassantException("en passant");

            } else if (isTrajectoryValid(movement) & board.isDistanceFree(movement)) {
                if (isPromotionMove(movement)) {
                    throw new PromotionException("Promotion");
                }
                throw new MoveOnEmptyCageException("default move");
            }
        } else {
            if (isFightTrajectoryValid(movement) & board.isDistanceFree(movement)) {
                if (isPromotionMove(movement)) {
                    throw new PromotionException("Promotion");
                }
                throw new BeatFigureException("beat figure move");
            }
        }
        throw new InvalidMoveException("Invalid move\n" + movement.getStart() + " - " + movement.getDestination());
    }

    private boolean isPromotionMove(Movable movement) {
        int horizontal = movement.getDestination().getHorizontal();

        return (color.equals(Color.White) && horizontal == 8)
                | (color.equals(Color.Black) && horizontal == 1);
    }

    private boolean isTwoCagesMove(final Movable movement) {
        Position startLast = movement.getStart();
        Position destinationLast = movement.getDestination();

        return Math.abs(startLast.getHorizontal() - destinationLast.getHorizontal()) == 2;
    }

    private boolean wasPawnEnPassant(MovementHistory lastMove, Movable movement) {
        if (lastMove == null) return false;
        ChessPiece piece = (ChessPiece) lastMove.start;
        boolean isSameVertical = lastMove.movement.getDestination().getVertical().equals(movement.getDestination().getVertical());
        boolean isSameHorizontal = lastMove.movement.getDestination().getHorizontal().equals(movement.getStart().getHorizontal());
        boolean answer = false;

        try {
            if (isFightTrajectoryValid(movement)
                    && piece instanceof Pawn
                    && isSameVertical
                    && isSameHorizontal
                    && isTwoCagesMove(lastMove.movement)) {
                answer = true;
            }
        } catch (ChessException ignored) {

        }
        return  answer;
    }
}
