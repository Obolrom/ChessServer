package com.company.server;

import com.example.customchess.engine.Game;
import com.example.customchess.engine.OneDeviceGame;
import com.example.customchess.engine.exceptions.*;
import com.example.customchess.engine.misc.Color;
import com.example.customchess.networking.ChessNetMovementPacket;
import com.example.customchess.networking.ChessNetPacket;
import com.example.customchess.networking.ConnectionPacket;

import java.io.IOException;
import java.util.concurrent.Callable;

public class ServerGame implements Callable<String> {
    public final int GAME_ID;
    private Game game;
    private final Client whitePlayer;
    private final Client blackPlayer;
    private final boolean restored;

    public ServerGame(Pair gamers) {
        this(gamers.getAndRemove(), gamers.getAndRemove());
    }

    public ServerGame(Client first, Client second) {
        this.GAME_ID = first.getGameID();
        if (first.isWhitePlayer()) {
            this.whitePlayer = first;
            this.blackPlayer = second;
        } else {
            this.whitePlayer = second;
            this.blackPlayer = first;
        }

        if (whitePlayer.isReconnection() && blackPlayer.isReconnection()) {
            System.out.println("DESERIALIZED GAME [" + GAME_ID + "]");
            restored = true;
            try {
                game = GameInstanceHandler.getInstance().retrieve(GAME_ID);
            } catch (Exception e) {
                game = new OneDeviceGame();
            }
        } else {
            restored = false;
            game = new OneDeviceGame();
        }
    }

    public boolean gameLoop() {
        Object packet;
        boolean isEndOfGame = false;
        Client currentPlayer;
        Client opponentPlayer;

        if (restored) {
            if (game.getCurrentPlayerTeam().equals(Color.White)) {
                currentPlayer = whitePlayer;
                opponentPlayer = blackPlayer;
            } else {
                currentPlayer = blackPlayer;
                opponentPlayer = whitePlayer;
            }
        } else {
            currentPlayer = whitePlayer;
            opponentPlayer = blackPlayer;
        }

        while ( ! isEndOfGame ) {
            try {
                packet = currentPlayer.receive();
                try {
                    processPacket(currentPlayer, opponentPlayer, packet);

                } catch (MoveOnEmptyCageException
                        | BeatFigureException
                        | CastlingException
                        | PromotionException
                        | PawnEnPassantException e) {
                    currentPlayer = currentPlayer == whitePlayer ? blackPlayer : whitePlayer;
                    opponentPlayer = opponentPlayer == whitePlayer ? blackPlayer : whitePlayer;
                } catch (CheckMateException | DrawException e) {
                    isEndOfGame = true;
                } catch (ChessException ignored) { }

            } catch (IOException | ClassNotFoundException e) {
                closeClients();
                System.err.println("game [id = " + GAME_ID + "] was broken");
                game.rollBackLastMove();
                if (GameInstanceHandler.getInstance().saveGame(GAME_ID, game)) {
                    System.out.println("Game [id = " + GAME_ID + "] was saved");
                }
                break;
            }
        }

        return isEndOfGame;
    }

    protected boolean sendResponseAboutGameStart() {
        try {
            whitePlayer.send(new ConnectionPacket(whitePlayer.getConnectionPacket()));
            blackPlayer.send(new ConnectionPacket(blackPlayer.getConnectionPacket()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected void closeClients() {
        try {
            whitePlayer.close();
            blackPlayer.close();
        } catch (IOException e) {
            System.out.println("clients disconnected");
        }
    }

    @Override
    public String call() throws Exception {
        if (sendResponseAboutGameStart()) {
            System.out.println("game started successfully");
        }
        System.out.println("game loop ended [" + gameLoop() + "]");
        System.out.println("game id " + GAME_ID + " deleted " +
                GameIDHolder.getInstance().remove(GAME_ID));

        return "done";
    }

    private void processPacket(Client currentPlayer, Client opponentPlayer,
                               Object packet) throws IOException, ChessException {
        if (packet.getClass() != ChessNetMovementPacket.class) return;
        ChessNetPacket chessPacket = (ChessNetPacket) packet;

        try {
            System.out.println(chessPacket);
            processChessNetPacket(chessPacket);

        } catch (MoveOnEmptyCageException
                | BeatFigureException
                | CastlingException
                | PromotionException
                | PawnEnPassantException
                | CheckMateException
                | DrawException e) {
            currentPlayer.send(chessPacket);
            opponentPlayer.send(chessPacket);
            if ( ! currentPlayer.isActive() | ! opponentPlayer.isActive()) {
                throw new IOException();
            }
            throw e;
        } catch (ChessException e) {
            // FIXME: 01.04.21 send kinda message packet about fuck up
            currentPlayer.send(packet);
        }
    }

    protected void processChessNetPacket(ChessNetPacket chessPacket) throws ChessException {
        try {
            System.out.println("\t" + chessPacket.getMovement());
            game.tryToMakeMovement(chessPacket.getMovement());

        } catch (MoveOnEmptyCageException
                | BeatFigureException
                | CastlingException
                | PromotionException
                | PawnEnPassantException
                | CheckMateException
                | DrawException e) {
            if (chessPacket.isPromotionPacket()) {
                game.promotion(chessPacket.getPromotionPiece());
            }
            chessPacket.makeMovementLegal();
            throw e;
        }
    }
}
