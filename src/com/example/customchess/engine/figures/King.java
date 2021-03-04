package com.example.customchess.engine.figures;


import com.example.customchess.engine.Board;
import com.example.customchess.engine.EndGameChecker;
import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.misc.Verticals;
import com.example.customchess.engine.exceptions.*;
import com.example.customchess.engine.movements.*;

public class King extends ChessPiece {

    public King(Color color, Position position) {
        super(position, 0.0, color);
    }

    @Override
    public String toString() {
        return "K";
    }

    @Override
    public boolean isTrajectoryValid(Movable movement) throws ChessException {
        Position start = movement.getStart();
        Position destination = movement.getDestination();

        int startVertical = start.getVertical().ordinal();
        int startHorizontal = start.getHorizontal() - 1;
        int destVertical = destination.getVertical().ordinal();
        int destHorizontal = destination.getHorizontal() - 1;

        int horizontalDiff = Math.abs(startHorizontal - destHorizontal);
        int verticalDiff = Math.abs(startVertical - destVertical);

        if (verticalDiff < 2 && horizontalDiff < 2) {
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
        Position start = movement.getStart();
        Position destination = movement.getDestination();

        if (!board.isCageEmpty(destinationFigure) && startFigure.hasSameColor(destinationFigure)) {
            throw new OneTeamPiecesSelectedException("One team pieces are selected");

        } else if (board.isCageEmpty(destinationFigure)) {
            Position cornerPiecePosition = getRookPositionOnFlank(destination);
            ChessPiece cornerPiece = ((ChessPiece) board.findBy(cornerPiecePosition));

            if (start.getHorizontal().equals(destination.getHorizontal())
                    && Math.abs(start.getVertical().ordinal() - destination.getVertical().ordinal()) == 2
                    && cornerPiece instanceof Rook
                    && (cornerPiece.firstMove & startFigure.firstMove)) {

                Position middle = getMiddleBetween(start, destination);
                if ( gameAnalyser.isPositionUnderAttackByEnemyTeam(color, start)
                        || gameAnalyser.isPositionUnderAttackByEnemyTeam(color, destination)
                        || gameAnalyser.isPositionUnderAttackByEnemyTeam(color, middle)) {
                    throw new CheckKingException(color + " King under attack : " + start);
                }
                if (board.isDistanceFree(new Movement(start, cornerPiecePosition))) {
                    throw new CastlingException("castling");
                }

            } else if (isTrajectoryValid(movement)
                    & board.isDistanceFree(movement)) {
                throw new MoveOnEmptyCageException("default move");
            }

        } else {
            if (isFightTrajectoryValid(movement)
                    & board.isDistanceFree(movement)) {
                throw new BeatFigureException("beat figure move");
            }
        }
        throw new InvalidMoveException("Invalid move\n" + movement.getStart() + " - " + movement.getDestination());
    }

    private Position getRookPositionOnFlank(Position onFlank) {
        Verticals vertical;
        if (onFlank.getVertical().ordinal() > 3) { // queen's flank
            vertical = Verticals.a;
        } else {
            vertical = Verticals.h;
        }

        return new BoardPosition(vertical, onFlank.getHorizontal());
    }

    private Position getMiddleBetween(Position start, Position destination) {
        Verticals vertical = Verticals.f;
        int diffVertical = start.getVertical().ordinal() - destination.getVertical().ordinal();
        if (diffVertical == -2) {
            vertical = Verticals.d;
        }

        return new BoardPosition(vertical, start.getHorizontal());
    }
}
