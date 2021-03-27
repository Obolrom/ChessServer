package com.example.customchess.engine;

import com.example.customchess.engine.automata.Player;
import com.example.customchess.engine.automata.WhitePlayer;
import com.example.customchess.engine.exceptions.BeatFigureException;
import com.example.customchess.engine.exceptions.CastlingException;
import com.example.customchess.engine.exceptions.CheckKingException;
import com.example.customchess.engine.exceptions.CheckMateException;
import com.example.customchess.engine.exceptions.ChessException;
import com.example.customchess.engine.exceptions.DrawException;
import com.example.customchess.engine.exceptions.FigureNotChosenException;
import com.example.customchess.engine.exceptions.MoveOnEmptyCageException;
import com.example.customchess.engine.exceptions.PawnEnPassantException;
import com.example.customchess.engine.exceptions.PromotionException;
import com.example.customchess.engine.figures.Bishop;
import com.example.customchess.engine.figures.ChessPiece;
import com.example.customchess.engine.figures.King;
import com.example.customchess.engine.figures.Knight;
import com.example.customchess.engine.figures.Pawn;
import com.example.customchess.engine.figures.Piece;
import com.example.customchess.engine.figures.Queen;
import com.example.customchess.engine.figures.Rook;
import com.example.customchess.engine.misc.Color;
import com.example.customchess.engine.movements.BoardPosition;
import com.example.customchess.engine.movements.Movable;
import com.example.customchess.engine.movements.MovementHistory;
import com.example.customchess.engine.movements.Position;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class OneDeviceGame implements Game {

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
