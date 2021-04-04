package com.company.server.requests;

import com.company.server.*;

import java.io.IOException;

public class ReconnectionHandler extends RequestHandler {
    private final WaitingList waitingList;
    private final GameIDHolder idHolder;
    private final GameInstanceHandler gameInstanceHandler;

    public ReconnectionHandler(WaitingList waitingList) {
        this.waitingList = waitingList;
        gameInstanceHandler = GameInstanceHandler.getInstance();
        idHolder = GameIDHolder.getInstance();
    }

    @Override
    public boolean handle(Client gamer) {
        try {
            int gameId = gamer.getGameID();
            if (gameInstanceHandler.hasGame(gameId) && !idHolder.hasSuchID(gameId)) {
                synchronized (waitingList) {
                    if (!waitingList.push(gamer)) {
                        System.out.println("[deleted] " + gamer);
//                    gamer.send(new Object());  // TODO: 27.03.21 send response packet
                        gamer.close();
                    }
                }
            } else {
                System.out.println("[deleted] " + gamer);
//                gamer.send(new Object());  // TODO: 27.03.21 send response packet
                gamer.close();
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
