package com.company.server;

import com.example.customchess.engine.misc.Team;
import com.example.customchess.networking.ConnectionPacket;
import com.example.customchess.networking.ConnectionType;
import com.example.customchess.networking.HeartBeatPacket;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient implements Client, Closeable {
    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final ConnectionPacket connectionPacket;

    public GameClient(Socket socket, int timeout) throws IOException, ClassNotFoundException {
        this.socket = socket;
//        socket.setSoTimeout(timeout);
        input = new ObjectInputStream(socket.getInputStream());
        output = new ObjectOutputStream(socket.getOutputStream());
        connectionPacket = (ConnectionPacket) input.readObject();
    }

    @Override
    public boolean isReconnection() {
        return connectionPacket.type.equals(ConnectionType.RECONNECT);
    }

    @Override
    public boolean isWhitePlayer() {
        return connectionPacket.team.equals(Team.White);
    }

    @Override
    public boolean isActive() {
        boolean isActive = true;
        try {
            send(new HeartBeatPacket());
            receive();
        } catch (IOException | ClassNotFoundException e) {
            isActive = false;
        }
        return socket != null && isActive;
    }

    @Override
    public Team getTeam() {
        return connectionPacket.team;
    }

    @Override
    public int getGameID() {
        return connectionPacket.GAME_ID;
    }

    @Override
    public ConnectionPacket getConnectionPacket() {
        return connectionPacket;
    }

    @Override
    public boolean isOpponent(Client other) {
        if (other.getClass() != GameClient.class) return false;
        GameClient enemy = (GameClient) other;
        boolean gameIdEquality = connectionPacket.GAME_ID == enemy.connectionPacket.GAME_ID;
        boolean teamEquality = connectionPacket.team.equals(enemy.connectionPacket.team);
        boolean connectionTypeEquality = connectionPacket.type
                .equals(enemy.connectionPacket.type);
        return gameIdEquality && !teamEquality && connectionTypeEquality;
    }

    @Override
    public boolean isSameConnectionType(Client other) {
        return connectionPacket.type.equals(((GameClient) other).connectionPacket.type);
    }

    @Override
    public Object receive() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

    @Override
    public void send(Object packet) throws IOException {
        output.writeObject(packet);
        output.flush();
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("[Client] " + socket.getLocalSocketAddress() + " is closed");
        }
    }
}
