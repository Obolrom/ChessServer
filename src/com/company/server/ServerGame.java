package com.company.server;

import com.example.customchess.engine.Game;
import com.example.customchess.engine.OneDeviceGame;
import com.example.customchess.engine.exceptions.*;
import com.example.customchess.networking.ChessNetMovementPacket;
import com.example.customchess.networking.ChessNetPacket;
import com.example.customchess.networking.ConnectionPacket;

import java.io.IOException;
import java.util.concurrent.Callable;

public class ServerGame implements Callable<String> {
    public final int GAME_ID;
    private final Game game;
    private final Client whitePlayer;
    private final Client blackPlayer;

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
            game = null;
        } else {
            game = new OneDeviceGame();
        }
    }

    public boolean gameLoop() {
        Object packet;
        boolean isEndOfGame = false;
        Client currentPlayer = whitePlayer;
        Client opponentPlayer = blackPlayer;

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
                System.err.println("fuck up");
                break;
            }
        }

        return isEndOfGame;
    }

    private boolean sendResponseAboutGameStart() {
        try {
            whitePlayer.send(new ConnectionPacket(whitePlayer.getConnectionPacket()));
            blackPlayer.send(new ConnectionPacket(blackPlayer.getConnectionPacket()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String call() throws Exception {
        if (sendResponseAboutGameStart()) {
            System.out.println("game started successfully");
        }
        System.out.println("game loop end = " + gameLoop());

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
                GameInstanceHandler.getInstance().saveGame(GAME_ID, game);
                throw new IOException();
            }
            throw e;
        } catch (ChessException e) {
            currentPlayer.send(packet);
        }
    }

    private void processChessNetPacket(ChessNetPacket chessPacket) throws ChessException {
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
