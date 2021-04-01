package com.company.server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

public class ClientDispatcher extends Thread {
    private final WaitingList clients;
    private final ThreadPoolExecutor pool;
    private final GameIDHolder idHolder;
    private final BlockingQueue<Socket> queue;

    public ClientDispatcher(BlockingQueue<Socket> queue, ThreadPoolExecutor pool) {
        super("ActiveClients");
        this.queue = queue;
        this.pool = pool;
        clients = new WaitingList();
        idHolder = GameIDHolder.getInstance();
    }

    private void addClient(Socket client) {
        try {
            GameClient gamer = new GameClient(client, 500);
            if (gamer.isReconnection()) {
                System.out.println("[RECONNECTION]");
            }
            if ( ! clients.push(gamer) | idHolder.hasSuchID(gamer.getGameID())) {
                System.out.println("[deleted] " + gamer);
//                gamer.send(new Object());  // TODO: 27.03.21 send response packet
                gamer.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[CLIENT] disconnected in time of connection");
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("active clients thread waiting...");

                if ( ! clients.isAnyReadyPair()) {
                    Socket client = queue.take();
                    addClient(client);
                    System.out.println("active clients thread is running");
                }
                createGames(clients.getReadyPairsForGame());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGames(List<Pair> ready) {
        System.out.println("\t[ready list size before] = " + ready.size());
        for (Pair pair : ready) {
            if (idHolder.hasSuchID(pair.getGameId())) {
                System.out.println("\tGAME CANNOT BE CREATED");
                // TODO: 30.03.21 response to clients
                continue;
            }
            if (pair.isReadyForGame() && idHolder.pushId(pair.getGameId())) {
                FutureTask<String> game = new FutureTask<>(new ServerGame(pair));
                pool.execute(game);
                clients.remove(pair);
                ready.remove(pair); // todo remove at the end
                System.out.println("game created with [ID: " + pair.getGameId() + "]");
            }
        }
        System.out.println("\t[ready list size after] = " + ready.size());
    }
}
