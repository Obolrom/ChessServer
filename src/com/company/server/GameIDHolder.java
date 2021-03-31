package com.company.server;

import java.util.*;

public class GameIDHolder {
    private static final GameIDHolder instance = new GameIDHolder();
    private final TreeSet<Integer> gameIDs;

    private GameIDHolder() {
        this.gameIDs = new TreeSet<>();
    }

    public synchronized static GameIDHolder getInstance() {
        return instance;
    }

    public synchronized boolean hasSuchID(int id) {
        return gameIDs.contains(id);
    }

    public synchronized boolean pushId(int id) {
        return ! hasSuchID(id) && gameIDs.add(id);
    }

    public synchronized void remove(int id) {
        gameIDs.remove(id);
    }
}
