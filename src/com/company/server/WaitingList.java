package com.company.server;

import java.util.*;

public class WaitingList {
    private final Hashtable<Integer, Pair> waitingClients;

    public WaitingList() {
        waitingClients = new Hashtable<>();
    }

    // TODO: 27.03.21 add checking for all unique game_idS on server
    // TODO: 27.03.21 maybe add throwing exceptions
    public boolean push(Client client) {
        boolean res = false;
        if (waitingClients.containsKey(client.GAME_ID)) {
            Pair pair = waitingClients.get(client.GAME_ID);
            if (pair != null) {
                res = pair.addClient(client);
            }
        } else {
            waitingClients.put(client.GAME_ID, new Pair(client));
            res = true;
        }
        return res;
    }

    public List<Pair> getReadyPairsForGame() {
        final List<Pair> readyForGame = new LinkedList<>();

        Set<Integer> keys = waitingClients.keySet();
        for (Integer key : keys) {
            Pair current = waitingClients.get(key);
            if (current != null && current.isReadyForGame()) {
                readyForGame.add(current);
            }
        }

        return readyForGame;
    }

    public void removeAllInactiveClients() {
        Set<Integer> keys = waitingClients.keySet();
        for (Integer key : keys) {
            Pair current = waitingClients.get(key);
            if (current != null) {
                current.removeInactiveClients();
                // TODO: 28.03.21 check for empty pair
            }
        }
    }

    public void remove(Pair pair) {
        if (pair != null) {
            waitingClients.remove(pair.getGameId());
        }
    }

    public boolean isAnyReadyPair() {
        Set<Integer> keys = waitingClients.keySet();
        for (Integer key : keys) {
            Pair current = waitingClients.get(key);
            if (current != null && current.isReadyForGame()) {
                return true;
            }
        }
        return false;
    }
}
