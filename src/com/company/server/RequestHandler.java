package com.company.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RequestHandler {
    private final ThreadPoolExecutor pool;
    private final int PORT;
    private final ClientDispatcher dispatcher;

    public RequestHandler(int port, int maxPoolSize) {
        PORT = port;
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxPoolSize);
        dispatcher = new ClientDispatcher(pool);
        dispatcher.start();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    System.out.println("[CLIENT] connected [address: " +
                            client.getInetAddress() + "]");

                    synchronized (dispatcher) {
                        dispatcher.addClient(client);
                        dispatcher.notify();
                    }
                } catch (IOException e) {
                    System.err.println("CLIENT'S CONNECTION DENIED");
                }
            }
        } catch (IOException e) {
            System.err.println("REQUEST CANNOT BE HANDLE");
        }
    }
}
