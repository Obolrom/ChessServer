package com.example.customchess.networking;

import com.example.customchess.engine.misc.Team;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class ConnectionPacket implements Serializable {
    private static final long serialVersionUID = 12452;
    public final Date time;
    public final Team team;
    public final int  GAME_ID;
    public final ConnectionType type;
    private boolean connected;

    public ConnectionPacket(Team team, int GAME_ID, ConnectionType type) {
        time = Calendar.getInstance().getTime();
        this.team = team;
        this.GAME_ID = GAME_ID;
        this.type = type;
        connected = false;
    }

    public boolean isConnectedToServer() {
        return connected;
    }

    @Override
    public String toString() {
        return "ConnectionPacket{" +
                "team=" + team +
                ", GAME_ID=" + GAME_ID +
                ", type=" + type +
                '}';
    }
}
