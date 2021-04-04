package com.company.server.requests;

import com.company.server.Client;

public abstract class RequestHandler {
    RequestHandler next;

    public RequestHandler() { }

    public abstract boolean handle(Client client);

    protected boolean checkNext(Client client) {
        if (next == null) {
            return true;
        }
        return next.checkNext(client);
    }
}
