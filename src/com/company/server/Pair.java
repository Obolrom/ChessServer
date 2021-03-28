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

    public void removeInactiveClients() {
        removeInactiveUser(first);
        removeInactiveUser(second);
    }

    public Client getSecond() {
        return second;
    }

    public boolean isReadyForGame() {
        return first != null && second != null
                && first.isActive() && second.isActive();
    }

    public boolean addClient(Client client) {
        if (first.GAME_ID == client.GAME_ID && !first.team.equals(client.team)) {
            second = client;
            return true;
        }
        return false;
    }

    private void removeInactiveUser(Client client) {
        // TODO: 28.03.21 remove sout
        if (client != null && !client.isActive()) {
            System.out.println(client.GAME_ID + " deleted");
            client = null;
        }
    }
}
