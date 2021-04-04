package com.company.server.requests;

import com.company.server.*;

public class ClientConnectionHandler extends RequestHandler {
    private final WaitingList waitingList;

    public ClientConnectionHandler(WaitingList waitingList) {
        this.waitingList = waitingList;
    }

    @Override
    public boolean handle(Client client) {
        RequestHandler requestHandler;
        if (client.isReconnection()) {
            requestHandler = new ReconnectionHandler(waitingList);
        } else {
            requestHandler = new ConnectionHandler(waitingList);
        }
        return requestHandler.handle(client);
    }
}
