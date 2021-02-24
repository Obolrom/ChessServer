package com.company.romeo.engine;

import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.misc.Verticals;
import com.company.romeo.engine.figures.ChessPiece;
import com.company.romeo.engine.figures.Piece;
import com.company.romeo.engine.movements.*;
import com.company.romeo.server.engine.movements.*;

import java.util.LinkedList;
import java.util.List;

public class Board {

    private Piece[][] matrix;

    public Board(Piece[][] matrix) {
        this.matrix = matrix;
    }

    public Board(List<Piece> blackTeam, List<Piece> whiteTeam) {
        matrix = new ChessPiece[8][8];
        placeTeam(whiteTeam);
        placeTeam(blackTeam);
    }

    public void castling(Position start, Position destination) {
        int horizontal = start.getHorizontal();
        int startVertical = start.getVertical().ordinal();

        Position oldRookPlace;
        Position newRookPlace;

        if ( BoardPosition.isShortSchemeCastling(destination) ) {
            oldRookPlace = new BoardPosition(Verticals.h, horizontal);
            newRookPlace = new BoardPosition(startVertical - 1, horizontal);

        } else {
            oldRookPlace = new BoardPosition(Verticals.a, horizontal);
            newRookPlace = new BoardPosition(startVertical + 1, horizontal);
        }
        findBy(oldRookPlace).setPosition(newRookPlace);
        findBy(start).setPosition(destination);
        put(destination, findBy(start));
        put(newRookPlace, findBy(oldRookPlace));
        hide(oldRookPlace);
        hide(start);
    }

    public void pawnOnThePass(Position start, Position destination) {
        Position oldAttackedPawn;

        if (((ChessPiece) findBy(start)).color.equals(Color.White)) {
            oldAttackedPawn = new BoardPosition(destination.getVertical().ordinal(),
                    destination.getHorizontal() - 1);
        } else {
            oldAttackedPawn = new BoardPosition(destination.getVertical().ordinal(),
                    destination.getHorizontal() + 1);
        }
        findBy(start).setPosition(destination);
        put(destination, findBy(start));
        hide(start);
        hide(oldAttackedPawn);
    }

    public void beatFigure(Position start, Position destination) {
        Piece startPiece = findBy(start);
        startPiece.setPosition(destination);
        put(destination, findBy(start));
        hide(start);
    }

    private void hide(Position position) {
        put(position, null);
    }

    private void put(Position position, Piece piece) {
        int vertical = position.getVertical().ordinal();
        int horizontal = position.getHorizontal() - 1;

        matrix[horizontal][vertical] = piece;
    }

    public void swapFigures(Position start, Position destination) {
        Piece startFigure = findBy(start);
        Piece destinationFigure = findBy(destination);
        startFigure.setPosition(destination);

        put(start, destinationFigure);
        put(destination, startFigure);
    }

    public boolean isDistanceFree(Movable movement) {
        LinkedList<Position> path = Movement.getPositionsOnDistance(movement);

        for (int i = 0; i < path.size(); i++) {
            if ( ! isCageEmpty(
                    findBy(path.get(i))) ) {
                return false;
            }
        }

        return true;
    }

    public void restorePreviousTurn(MovementHistory history) {
        Position start = history.movement.getStart();
        Position destination = history.movement.getDestination();
        if ( findBy(destination) != null ) {
            findBy(destination).setPosition(start);
        }
        if ( findBy(start) != null ) {
            findBy(start).setPosition(destination);
        }
        put(start, history.start);
        put(destination, history.destination);
    }

    public void restoreRemovedFigure(Piece piece) {
        put(piece.getCurrentPosition(), piece);
    }

    public void promoteTo(Position position, Piece promotion) {
        put(position, promotion);
    }

    public Piece findBy(Position position) {
        int vertical = position.getVertical().ordinal();
        int horizontal = position.getHorizontal() - 1;

        return matrix[horizontal][vertical];
    }

    public boolean isCageEmpty(Piece figure) {
        return figure == null;
    }

    public void promotion(Position start, Position destination) {
        if ( ! isCageEmpty(findBy(destination)) ) {
            beatFigure(start, destination);
        } else {
            swapFigures(start, destination);
        }
    }

    public void placeTeam(List<Piece> team) {
        Piece currentPiece;
        for (int i = 0; i < team.size(); i++) {
            currentPiece = team.get(i);
            put(currentPiece.getCurrentPosition(), currentPiece);
        }
    }

}