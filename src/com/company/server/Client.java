package com.company.server;

import com.example.customchess.engine.misc.Team;
import com.example.customchess.networking.ConnectionPacket;
import com.example.customchess.networking.HeartBeatPacket;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client implements Closeable {
    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    public final Team team;
    public final int GAME_ID;

    public Client(Socket socket, int timeout) throws IOException, ClassNotFoundException {
        this.socket = socket;
        socket.setSoTimeout(timeout);
        input = new ObjectInputStream(socket.getInputStream());
        output = new ObjectOutputStream(socket.getOutputStream());
        ConnectionPacket connection = (ConnectionPacket) input.readObject();
        team = connection.team;
        GAME_ID = connection.GAME_ID;
    }

    @Override
    public int hashCode() {
        return GAME_ID;
    }

    public boolean isWhitePlayer() {
        return team.equals(Team.White);
    }

    public boolean isActive() {
        boolean isActive = true;
        try {
            send(new HeartBeatPacket());
            receive();
        } catch (IOException | ClassNotFoundException e) {
            isActive = false;
        }
        return socket != null && socket.isConnected() && isActive;
    }

    public Object receive() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

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
