package tests;

import com.company.server.Client;
import com.example.customchess.engine.misc.Team;
import com.example.customchess.networking.ConnectionPacket;

import java.io.IOException;

public class ClientForTestReasons implements Client {
    public final Team team;
    public final int GAME_ID;
    public boolean isActive;

    public ClientForTestReasons(ConnectionPacket packet) {
        this.team = packet.team;
        this.GAME_ID = packet.GAME_ID;
        isActive = true;
    }

    public void enable() {
        isActive = true;
    }

    public void disable() {
        isActive = false;
    }

    @Override
    public boolean isWhitePlayer() {
        return team.equals(Team.White);
    }

    @Override
    public Object receive() throws IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public void send(Object packet) throws IOException {

    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public int getGameID() {
        return GAME_ID;
    }
}
