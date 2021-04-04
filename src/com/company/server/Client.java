package com.company.server;

import com.example.customchess.engine.misc.Team;
import com.example.customchess.networking.ConnectionPacket;
import com.example.customchess.networking.ConnectionType;

import java.io.Closeable;
import java.io.IOException;

public interface Client extends Closeable {
    boolean isWhitePlayer();
    Object receive() throws IOException, ClassNotFoundException;
    void send(Object packet) throws IOException;
    boolean isActive();
    Team getTeam();
    int getGameID();
    ConnectionPacket getConnectionPacket();
    boolean isOpponent(Client other);
    boolean isReconnection();
}
