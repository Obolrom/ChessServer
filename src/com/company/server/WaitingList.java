package com.company.server;

import java.util.*;

public class WaitingList {
    private final Hashtable<Integer, Pair> waitingClients;

    public WaitingList() {
        waitingClients = new Hashtable<>();
    }

    public boolean push(Client client) {
        boolean res = false;
        removeAllInactiveClients();
        if (waitingClients.containsKey(client.getGameID())) {
            Pair pair = waitingClients.get(client.getGameID());
            if (pair != null) {
                res = pair.addClient(client);
            }
        } else {
            waitingClients.put(client.getGameID(), new Pair(client));
            res = true;
        }
        return res;
    }

    public List<Pair> getReadyPairsForGame() {
        final List<Pair> readyForGame = new LinkedList<>();

        removeAllInactiveClients();
        Set<Integer> keys = waitingClients.keySet();
        for (Integer key : keys) {
            Pair current = waitingClients.get(key);
            if (current != null && current.isReadyForGame()) {
                readyForGame.add(current);
            }
        }

        return readyForGame;
    }

    protected void removeAllInactiveClients() {
        Set<Integer> keys = waitingClients.keySet();
        for (Integer key : keys) {
            Pair current = waitingClients.get(key);
            if (current != null) {
                current.removeInactiveClients();
                if (current.isEmpty()) waitingClients.remove(key);
            }
        }
    }

    public void remove(Pair pair) {
        if (pair != null) {
            waitingClients.remove(pair.getGameId());
        }
    }

    public boolean isAnyReadyPair() {
        return getReadyPairsForGame().size() > 0;
    }
}
