package com.company.server;

import com.example.customchess.engine.misc.Team;

import java.util.ArrayList;
import java.util.List;

public class Pair {
    private final List<Client> clients;
    private final int GAME_ID;
    private final Team team;

    public Pair(Client first) {
        clients = new ArrayList<>(2);
        GAME_ID = first.getGameID();
        team = first.getTeam();
        clients.add(first);
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }

    public int getGameId() {
        return GAME_ID;
    }

    public Team getTeam() {
        return team;
    }

    public Client getAndRemove() {
        Client client = clients.get(0);
        clients.remove(0);
        return client;
    }

    public void removeInactiveClients() {
        clients.removeIf(client -> client != null && !client.isActive());
    }

    public boolean isReadyForGame() {
        if (clients.size() != 2) return false;
        Client first = clients.get(0);
        Client second = clients.get(1);
        return first != null && second != null
                && first.isActive() && second.isActive();
    }

    public boolean addClient(Client client) {
        if (clients.size() != 1) return false;
        Client first = clients.get(0);
        if (first.isOpponent(client)) {
            clients.add(client);
            return true;
        }
        return false;
    }
}
