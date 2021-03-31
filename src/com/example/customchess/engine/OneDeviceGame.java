package com.example.customchess.engine;

import com.example.customchess.engine.automata.*;
import com.example.customchess.engine.exceptions.*;
import com.example.customchess.engine.figures.*;
import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.movements.*;

import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;


public class OneDeviceGame implements Game {
    private static final long serialVersionUID = 120548367493275L;
    private final Board board;
    private Player currentPlayer;
    private final Stack<MovementHistory> movementStack;
    private final EndGameChecker    gameAnalyser;

    public OneDeviceGame() {
        movementStack = new Stack<>();
        currentPlayer = new WhitePlayer(this);
        Map<Position, Piece> whiteTeam = new Hashtable<>(16);
        Map<Position, Piece> blackTeam = new Hashtable<>(16);
        initTeam(blackTeam, Color.Black);
        initTeam(whiteTeam, Color.White);
        board = new Board(blackTeam, whiteTeam);
        gameAnalyser  = new EndGameChecker(board);
    }

    private void initTeam(Map<Position, Piece> team, Color color) {
        int pawnHorizontal = 2;
        int kingHorizontal = 1;
        if (Color.Black.equals(color)) {
            pawnHorizontal = 7;
            kingHorizontal = 8;
        }
        team.put(new BoardPosition(0, kingHorizontal), new Rook(color));
        team.put(new BoardPosition(7, kingHorizontal), new Rook(color));
        team.put(new BoardPosition(1, kingHorizontal), new Knight(color));
        team.put(new BoardPosition(6, kingHorizontal), new Knight(color));
        team.put(new BoardPosition(2, kingHorizontal), new Bishop(color));
        team.put(new BoardPosition(5, kingHorizontal), new Bishop(color));
        team.put(new BoardPosition(3, kingHorizontal), new King(color));
        team.put(new BoardPosition(4, kingHorizontal), new Queen(color));
        for (int vertical = 0; vertical < 8; vertical++) {
            team.put(new BoardPosition(vertical, pawnHorizontal), new Pawn(color));
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
            throw new DrawException();
        }
    }

    @Override
    public void checkForCheckMate() throws CheckMateException {
        if (gameAnalyser.isCheckMate(currentPlayer.getColor())) {
            throw new CheckMateException();
        }
    }

    @Override
    public void promotion(String choice) {
        Piece promotedPiece;
        Color team = ((ChessPiece) movementStack.peek().start).color;
        Position destination = movementStack.peek().movement.getDestination();

        switch (choice) {
            case "Queen":
                promotedPiece = new Queen(team);
                break;
            case "Bishop":
                promotedPiece = new Bishop(team);
                break;
            case "Rook":
                promotedPiece = new Rook(team);
                break;
            default:
                promotedPiece = new Knight(team);
                break;
        }

        board.promoteTo(destination, promotedPiece);
    }

    @Override
    public void tryToMakeMovement(Movable movement) throws ChessException {
        Position start = movement.getStart();
        Position destination = movement.getDestination();
        Piece startFigure = board.findBy(movement.getStart());
        Piece destinationFigure = board.findBy(movement.getDestination());  // can be null
        MovementHistory currentMovementHeader = new MovementHistory(movement, startFigure, destinationFigure);

        try {
            if (currentPlayer.isCorrectPlayerMove((ChessPiece) startFigure)) {
                try {
                    startFigure.tryToMove(movement, board, gameAnalyser, getLastMovement());

                } catch (MoveOnEmptyCageException mec) {
                    board.swapFigures(start, destination);
                    throw mec;
                } catch (BeatFigureException bfe) {
                    board.beatFigure(start, destination);
                    throw bfe;
                } catch (CastlingException ce) {
                    board.castling(start, destination);
                    throw ce;
                } catch (PawnEnPassantException ppe) {
                    board.pawnOnThePass(start, destination);
                    throw ppe;
                } catch (PromotionException pe) {
                    board.promotion(start, destination);
                    throw pe;
                }
            }
        } catch (MoveOnEmptyCageException
                | BeatFigureException
                | CastlingException
                | PromotionException
                | PawnEnPassantException ce) {
            if (gameAnalyser.isKingUnderAttack(currentPlayer.getColor())) {
                board.restorePreviousTurn(currentMovementHeader);
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
}
