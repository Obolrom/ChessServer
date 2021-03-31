package com.company.server;

import com.example.customchess.engine.misc.Team;

import java.io.IOException;

public interface Client {
    boolean isWhitePlayer();
    Object receive() throws IOException, ClassNotFoundException;
    void send(Object packet) throws IOException;
    boolean isActive();
    Team getTeam();
    int getGameID();
}
