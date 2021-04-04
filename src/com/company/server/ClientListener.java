package com.company.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ClientListener {
    private final int PORT;
    private final BlockingQueue<Socket> blockingQueue;

    public ClientListener(int port, int maxPoolSize) {
        blockingQueue = new LinkedBlockingQueue<>();
        PORT = port;
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxPoolSize);
        ClientDispatcher dispatcher = new ClientDispatcher(blockingQueue, pool);
        dispatcher.start();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    System.out.println("[CLIENT] connected [address: " +
                            client.getInetAddress() + "]");

                    blockingQueue.put(client);

                } catch (IOException | InterruptedException e) {
                    System.err.println("CLIENT'S CONNECTION DENIED");
                }
            }
        } catch (IOException e) {
            System.err.println("REQUEST CANNOT BE HANDLE");
        }
    }
}
