package com.company.server.requests;

import com.company.server.*;

import java.io.IOException;

public class ConnectionHandler extends RequestHandler {
    private final WaitingList waitingList;
    private final GameIDHolder idHolder;
    private final GameInstanceHandler gameInstanceHandler;

    public ConnectionHandler(WaitingList waitingList) {
        this.waitingList = waitingList;
        gameInstanceHandler = GameInstanceHandler.getInstance();
        idHolder = GameIDHolder.getInstance();
    }

    @Override
    public boolean handle(Client gamer) {
        try {
            int gameId = gamer.getGameID();
            if (idHolder.hasSuchID(gameId) | gameInstanceHandler.hasGame(gameId)) {
                System.out.println("[deleted] " + gamer);
//                gamer.send(new Object());  // TODO: 27.03.21 send response packet
                gamer.close();
            } else {
                synchronized (waitingList) {
                    if (!waitingList.push(gamer)) {
                        System.out.println("[deleted] " + gamer);
                        gamer.close();
                    }
                }
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
