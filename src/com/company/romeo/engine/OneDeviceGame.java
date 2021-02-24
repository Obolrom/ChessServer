package com.company.romeo.engine;


import com.company.romeo.engine.automata.Player;
import com.company.romeo.engine.automata.WhitePlayer;
import com.company.romeo.engine.exceptions.*;
import com.company.romeo.engine.figures.*;
import com.company.romeo.engine.misc.Color;
import com.company.romeo.engine.misc.Verticals;
import com.company.romeo.engine.movements.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class OneDeviceGame implements Game {

    private Board board;
    private Player currentPlayer;
    private Stack<MovementHistory> movementStack;
    private List<Piece> blackTeam;
    private List<Piece> whiteTeam;
    private EndGameChecker    gameAnalyser;

    public OneDeviceGame() {
        movementStack = new Stack<>();
        currentPlayer = new WhitePlayer(this);
        whiteTeam = new LinkedList<>();
        blackTeam = new LinkedList<>();
        initTeam(blackTeam, Color.Black);
        initTeam(whiteTeam, Color.White);
        board = new Board(blackTeam, whiteTeam);
        gameAnalyser  = new EndGameChecker(board, whiteTeam, blackTeam);
    }

    // for debug
    public String getMovementsHistory() {
        if ( movementStack.isEmpty() ) return "empty";
        List<MovementHistory> correct = new LinkedList<>();

        StringBuilder stringBuilder = new StringBuilder();
        while ( ! movementStack.isEmpty()) {
            correct.add(movementStack.pop());
        }
        int j = 1;
        for (int i = correct.size() - 1; i >= 0; i--) {
            stringBuilder.append(j++).append(". ").append(correct.get(i).movement).append("\n");
        }
        for (int i = correct.size() - 1; i >= 0; i--) {
            movementStack.push(correct.get(i));
        }

        return stringBuilder.toString();
    }

    private void initTeam(List<Piece> team, Color color) {
        int pawnRow = 1;
        int kingRow = 0;
        if (Color.Black.equals(color)) {
            pawnRow = 6;
            kingRow = 7;
        }
        team.add(new Rook(color, new BoardPosition(Verticals.h, kingRow + 1)));
        team.add(new Rook(color, new BoardPosition(Verticals.a, kingRow + 1)));
        team.add(new Knight(color, new BoardPosition(Verticals.g, kingRow + 1)));
        team.add(new Knight(color, new BoardPosition(Verticals.b, kingRow + 1)));
        team.add(new Bishop(color, new BoardPosition(Verticals.c, kingRow + 1)));
        team.add(new Bishop(color, new BoardPosition(Verticals.f, kingRow + 1)));
        team.add(new King(color, new BoardPosition(Verticals.e, kingRow + 1)));
        team.add(new Queen(color, new BoardPosition(Verticals.d, kingRow + 1)));
        for (int vertical = 0; vertical < 8; vertical++) {
            team.add(new Pawn(color, new BoardPosition(vertical, pawnRow + 1)));
        }
    }

    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    public MovementHistory getLastMovement() {
        return ! movementStack.isEmpty() ? movementStack.peek() : null;
    }

    @Override
    public void checkForPat() throws DrawException {
        if (gameAnalyser.checkForDraw(currentPlayer.getColor())) {
            throw new DrawException("Draw");
        }
    }

    @Override
    public void checkForCheckMate() throws CheckMateException {
        if (gameAnalyser.isCheckMate(currentPlayer.getColor())) {
            throw new CheckMateException("Mate on the board\n" + currentPlayer.getColor() + " is fucked");
        }
    }

    @Override
    public void promotion(String choice) {
        Piece promotedPiece;
        Color team = ((ChessPiece) movementStack.peek().start).color;
        List<Piece> promTeam = team.equals(Color.White) ? whiteTeam : blackTeam;
        Position destination = movementStack.peek().movement.getDestination();

        switch (choice) {
            case "Queen":
                promotedPiece = new Queen(team, destination);
                break;
            case "Bishop":
                promotedPiece = new Bishop(team, destination);
                break;
            case "Rook":
                promotedPiece = new Rook(team, destination);
                break;
            default:
                promotedPiece = new Knight(team, destination);
                break;
        }

        removePieceFromTeam(board.findBy(destination));
        promTeam.add(promotedPiece);
        board.promoteTo(destination, promotedPiece);
    }

    private void removePieceFromTeam(Piece piece) {
        if (piece == null)
            return;
        List<Piece> team = piece.getColor().equals(Color.White) ? whiteTeam : blackTeam;
        team.remove(piece);
    }

    @Override
    public void tryToMakeMovement(Movable movement) throws ChessException {
        Position start = movement.getStart();
        Position destination = movement.getDestination();
        Piece startFigure = board.findBy(movement.getStart());
        Piece destinationFigure = board.findBy(movement.getDestination());  // can be null
        MovementHistory currentMovementHeader = new MovementHistory(movement, startFigure, destinationFigure);
        MovementHistory backUpCastling = currentMovementHeader;
        Piece backUpPiece = null;

        try {
            if (currentPlayer.isCorrectPlayerMove((ChessPiece) startFigure)) {
                try {
                    startFigure.tryToMove(movement, board, gameAnalyser, getLastMovement());

                } catch (MoveOnEmptyCageException mec) {
                    board.swapFigures(start, destination);
                    throw mec;
                } catch (BeatFigureException bfe) {
                    board.beatFigure(start, destination);
                    backUpPiece = destinationFigure;
                    removePieceFromTeam(destinationFigure);
                    throw bfe;
                } catch (CastlingException ce) {
                    Position oldRookPosition = start.getRookPositionOnFlank();
                    Position newRookPosition = oldRookPosition.getRookPositionOnFlankAfterCastling();
                    backUpPiece = board.findBy(oldRookPosition);
                    Piece afterCastling = board.findBy(newRookPosition);
                    backUpCastling = new MovementHistory(new Movement(oldRookPosition, newRookPosition), backUpPiece, afterCastling);
                    board.castling(start, destination);
                    throw ce;
                } catch (PawnEnPassantException ppe) {
                    Piece beatenPawn = board.findBy(destination.getPawnBeatenOnPassPosition((startFigure.getColor())));
                    backUpPiece = beatenPawn;
                    removePieceFromTeam(beatenPawn);
                    board.pawnOnThePass(start, destination);
                    throw ppe;
                } catch (PromotionException pe) {
                    board.promotion(start, destination);
                    backUpPiece = destinationFigure;
                    removePieceFromTeam(destinationFigure);
                    throw pe;
                }
            }

        } catch (MoveOnEmptyCageException
                | BeatFigureException
                | CastlingException
                | PromotionException
                | PawnEnPassantException ce) {
            if (gameAnalyser.isKingUnderAttack(currentPlayer.getColor())) {
                restoreInTeamAndOnBoard(backUpPiece);
                board.restorePreviousTurn(currentMovementHeader);
                if (ce instanceof CastlingException) {
                    board.restorePreviousTurn(backUpCastling);
                }
                throw new CheckKingException(currentPlayer.getColor() + " King under check");
            }
            startFigure.move();
            if (destinationFigure != null) destinationFigure.move();
            currentPlayer.changePlayer();
            movementStack.push(currentMovementHeader);
            throw ce;
        } catch (NullPointerException npe) {
            throw new FigureNotChosenException("Figure was not chosen");
        }
    }

    private void restoreInTeamAndOnBoard(Piece piece) {
        if (piece == null) return;
        List<Piece> team = piece.getColor().equals(Color.White) ? whiteTeam : blackTeam;
        team.add(piece);
        board.restoreRemovedFigure(piece);
    }
}
