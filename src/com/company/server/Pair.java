package com.company.server;

import com.example.customchess.engine.misc.Team;

public class Pair {
    private Client first;
    private Client second;

    public Pair(Client first) {
        this.first = first;
    }

    public int getGameId() {
        return first.GAME_ID;
    }

    public Team getTeam() {
        return first.team;
    }

    public Client getFirst() {
        return first;
    }

    public Client getSecond() {
        return second;
    }

    public boolean isReadyForGame() {
        return first != null && second != null
                && first.isActive() && second.isActive();
    }

    public void addClient(Client client) {
        if (first.GAME_ID == client.GAME_ID && !first.team.equals(client.team)) {
            second = client;
        }
    }
}
