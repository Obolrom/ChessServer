package com.company.server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

public class ClientDispatcher extends Thread {
    private final WaitingList clients;
    private final ThreadPoolExecutor pool;

    public ClientDispatcher(ThreadPoolExecutor pool) {
        super("ActiveClients");
        this.pool = pool;
        clients = new WaitingList();
    }

    public void addClient(Socket client) {
        try {
            // TODO: 27.03.21 create client in other thread
            Client gamer = new Client(client, 500);
            if ( ! clients.push(gamer)) {
                System.out.println("fuck off " + gamer);
                gamer.close();
//                gamer.send(new Object());  // TODO: 27.03.21 send response packet
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("active clients thread waiting...");

                if ( ! clients.isAnyReadyPair()) {
                    synchronized (this) {
                        wait();
                        System.out.println("active clients thread is running");
                    }
                }
                clients.removeAllInactiveClients();
                createGames(clients.getReadyPairsForGame());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGames(List<Pair> ready) {
        for (Pair pair : ready) {
            if (pair.isReadyForGame()) {
                FutureTask<String> game = new FutureTask<>(new ServerGame(pair));
                pool.execute(game);
                clients.remove(pair);
                System.out.println("game created with [ID: " + pair.getGameId() + "]");
            }
        }
    }
}
