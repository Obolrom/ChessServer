package com.example.customchess.engine;


import com.example.customchess.engine.figures.*;
import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.misc.Verticals;
import com.example.customchess.engine.movements.BoardPosition;
import com.example.customchess.engine.movements.Movable;
import com.example.customchess.engine.movements.Movement;
import com.example.customchess.engine.movements.MovementHistory;
import com.example.customchess.engine.movements.Position;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Board {

    private Piece[][] matrix;

    public Board(Piece[][] matrix) {
        this.matrix = matrix;
    }

    public Board(Map<Position, Piece> blackTeam, Map<Position, Piece> whiteTeam) {
        matrix = new ChessPiece[8][8];
        placeTeam(whiteTeam);
        placeTeam(blackTeam);
    }

    public Position getPositionOfPiece(Piece piece) {
        Piece currentPiece;
        Position currentPosition;
        Verticals[] verticals = Verticals.values();
        for (Verticals vertical : verticals) {
            for (int horizontal = 1; horizontal < 9; horizontal++) {
                currentPosition = new BoardPosition(vertical, horizontal);
                currentPiece = findBy(currentPosition);
                if ( ! isCageEmpty(currentPiece) && currentPiece == piece ) {
                    return currentPosition;
                }
            }
        }
        return null;
    }

    public List<Piece> getTeamBy(Color teamColor) {
        List<Piece> team = new LinkedList<>();
        Piece currentPiece;
        Position currentPosition;
        Verticals[] verticals = Verticals.values();
        for (Verticals vertical : verticals) {
            for (int horizontal = 1; horizontal < 9; horizontal++) {
                currentPosition = new BoardPosition(vertical, horizontal);
                currentPiece = findBy(currentPosition);
                if ( ! isCageEmpty(currentPiece) && currentPiece.getColor().equals(teamColor)  ) {
                    team.add(currentPiece);
                }
            }
        }
        return team;
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
        put(destination, findBy(start));
        hide(start);
        hide(oldAttackedPawn);
    }

    public void beatFigure(Position start, Position destination) {
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

        put(start, history.start);
        put(destination, history.destination);
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

    public void placeTeam(Map<Position, Piece> team) {
        Piece currentPiece;
        Set<Position> keys = team.keySet();
        for (Position position : keys) {
            currentPiece = team.get(position);
            put(position, currentPiece);
        }
    }
}