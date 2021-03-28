package com.company.server;

import com.example.customchess.engine.Game;
import com.example.customchess.engine.OneDeviceGame;
import com.example.customchess.engine.exceptions.*;
import com.example.customchess.networking.ChessNetPacket;

import java.io.IOException;
import java.util.concurrent.Callable;

public class ServerGame implements Callable<String> {
    public final int GAME_ID;
    private final Game game;
    private final Client whitePlayer;
    private final Client blackPlayer;

    public ServerGame(Pair gamers) {
        this(gamers.getFirst(), gamers.getSecond());
    }

    public ServerGame(Client first, Client second) {
        this.GAME_ID = first.GAME_ID;
        if (first.isWhitePlayer()) {
            this.whitePlayer = first;
            this.blackPlayer = second;
        } else {
            this.whitePlayer = second;
            this.blackPlayer = first;
        }

        game = new OneDeviceGame();
    }

    public void start() {
        process();
    }

    public void process() {
        try {
            while (true) {
                Thread.sleep(100000);
//                ChessNetPacket packet = null;
//                try {
//                    packet = (ChessNetPacket) whitePlayer.receive();
//                } catch (ClassNotFoundException | ClassCastException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("[WHITE CLIENT] message: " + packet);
//
//                try {
//                    assert packet != null;
//                    game.tryToMakeMovement(packet.getMovement());
//                } catch (MoveOnEmptyCageException
//                        | BeatFigureException
//                        | CastlingException
//                        | PromotionException
//                        | PawnEnPassantException e) {
//                    packet.makeMovementLegal();
//                } catch (ChessException ignored) { }
//
//                whitePlayer.send(packet);
//                blackPlayer.send(packet);
            }
        } catch (/*IOException |*/ InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String call() throws Exception {
        process();

        return null;
    }
}
