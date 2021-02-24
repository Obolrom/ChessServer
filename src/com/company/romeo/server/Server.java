package com.company.romeo.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

public class Server {
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
//            outputStream = new DataOutputStream(client.getOutputStream());
            inputStream = new DataInputStream(client.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                receive();
            }
        }).start();
    }

    public void receive() {
        try {
            while (true) {
                String string = inputStream.readUTF();
                System.out.println("[CLIENT] message: " + string);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server(3535).start();
    }
}
