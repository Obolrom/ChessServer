package com.company.romeo.server;

import com.company.romeo.engine.Game;
import com.company.romeo.engine.OneDeviceGame;
import com.company.romeo.engine.exceptions.*;
import com.company.romeo.engine.misc.Verticals;
import com.company.romeo.engine.movements.BoardPosition;
import com.company.romeo.engine.movements.Movement;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

public class Server {
    private Game   game;
    private Socket client;
    private int    port;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public Server(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.port = port;
            System.out.println("[SERVER] has started on port: " + port +
                    " | " + Calendar.getInstance().getTime());
            client = serverSocket.accept();
            System.out.println("[CLIENT] connected: " + client.getInetAddress());
            outputStream = new DataOutputStream(client.getOutputStream());
            inputStream = new DataInputStream(client.getInputStream());

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
                String string = inputStream.readUTF();
                System.out.println("[CLIENT] message: " + string);

                try {
                    game.tryToMakeMovement(
                            new Movement(
                                    new BoardPosition(Verticals.e, 2),
                                    new BoardPosition(Verticals.e, 4)));
                } catch (MoveOnEmptyCageException
                        | BeatFigureException
                        | CastlingException
                        | PromotionException
                        | PawnEnPassantException ignored) {

                } catch (ChessException e) {
                    outputStream.writeUTF("error");
                    outputStream.flush();
                }

                outputStream.writeUTF("ok");
                outputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server(3535).start();
    }
}
