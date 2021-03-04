package com.company.server;

import com.example.customchess.engine.Game;
import com.example.customchess.engine.OneDeviceGame;
import com.example.customchess.engine.exceptions.*;
import com.example.customchess.networking.ChessNetMovementPacket;
import com.example.customchess.networking.ChessNetPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerGame {
    private Game game;
    private Socket whitePlayerClient;
    private Socket blackPlayerClient;
    private int    port;
    private ObjectOutputStream whiteOutputStream;
    private ObjectInputStream whiteInputStream;
    private ObjectOutputStream blackOutputStream;
    private ObjectInputStream blackInputStream;

    public ServerGame(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.port = port;
            whitePlayerClient = serverSocket.accept();
            System.out.println("[CLIENT] white player connected: "
                    + whitePlayerClient.getLocalSocketAddress());
//            blackPlayerClient = serverSocket.accept();
//            System.out.println("[CLIENT] black player connected: "
//                    + blackPlayerClient.getLocalSocketAddress());
            whiteOutputStream = new ObjectOutputStream(whitePlayerClient.getOutputStream());
            whiteInputStream = new ObjectInputStream(whitePlayerClient.getInputStream());
//            blackOutputStream = new ObjectOutputStream(blackPlayerClient.getOutputStream());
//            blackInputStream = new ObjectInputStream(blackPlayerClient.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        game = new OneDeviceGame();
    }

    public void start() {
        process();
    }

    public void process() {
        try {
            while (true) {
                ChessNetPacket packet = null;
                try {
                    packet = (ChessNetMovementPacket) whiteInputStream.readObject();
                } catch (ClassNotFoundException | ClassCastException e) {
                    e.printStackTrace();
                }
                System.out.println("[WHITE CLIENT] message: " + packet);

                try {
                    assert packet != null;
                    game.tryToMakeMovement(packet.getMovement());
                } catch (MoveOnEmptyCageException
                        | BeatFigureException
                        | CastlingException
                        | PromotionException
                        | PawnEnPassantException e) {
                    packet.makeMovementLegal();
                } catch (ChessException ignored) { }

                whiteOutputStream.writeObject(packet);
                whiteOutputStream.flush();
//                blackOutputStream.writeObject(packet);
//                blackOutputStream.flush();

//                try {
//                    packet = (ChessNetMovementPacket) blackInputStream.readObject();
//                } catch (ClassNotFoundException | ClassCastException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("[BLACK CLIENT] message: " + packet);
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
//                blackOutputStream.writeObject(packet);
//                blackOutputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
