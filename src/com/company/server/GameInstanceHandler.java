package com.company.server;

import com.example.customchess.engine.Game;

import java.io.*;
import java.util.Hashtable;
import java.util.Map;

public class GameInstanceHandler {
    private static GameInstanceHandler instance;
    private static final String fileNamePrefix = "game";
    private final String absoluteGameInstancesPath;
    private final Map<Integer, File> gameInstances;

    private GameInstanceHandler() {
        absoluteGameInstancesPath = Server.directoryPath + Server.savedGamesPath;
        File directory = new File(absoluteGameInstancesPath);
        gameInstances = new Hashtable<>();
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile()) {
                try {
                    int gameId = getIdFromFileName(file.getName());
                    gameInstances.put(gameId, file);
                    System.out.println(file.getName());
                } catch (NumberFormatException ignored) { }
            }
        }
    }

    public synchronized static GameInstanceHandler getInstance() {
        if (instance == null) {
            instance = new GameInstanceHandler();
        }
        return instance;
    }

    public synchronized boolean saveGame(int gameId, Game game) {
        final String newFileName = "/" + fileNamePrefix + gameId + ".txt";
        File file = new File(absoluteGameInstancesPath + newFileName);
        try {
            if ( ! file.createNewFile()) return false;
            try (ObjectOutputStream output =
                         new ObjectOutputStream(new FileOutputStream(file))) {
                output.writeObject(game);
                output.flush();
            }
            gameInstances.put(gameId, file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized boolean hasGame(int gameId) {
        return gameInstances.containsKey(gameId);
    }

    public synchronized Game retrieve(int gameId) throws Exception {
        if ( ! gameInstances.containsKey(gameId))
            throw new Exception("Game with [id = " + gameId + "] doesn't exit");
        File file = gameInstances.remove(gameId);
        try (ObjectInputStream inputStream = new ObjectInputStream(
                new FileInputStream(file))) {
            Game retrieved = (Game) inputStream.readObject();
            if (file.delete()) System.out.println(file.getName() + " deleted");
            return retrieved;

        } catch (IOException | ClassCastException e) {
            throw new Exception("Game with [id = " + gameId + "] doesn't exit");
        }
    }

    private int getIdFromFileName(String fileName) {
        String subbedString = getSubbedNumber(fileName);
        return Integer.parseInt(subbedString);
    }

    private String getSubbedNumber(String sample) {
        char[] chars = sample.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char symbol : chars) {
            if (Character.isDigit(symbol)){
                stringBuilder.append(symbol);
            }
        }
        return new String(stringBuilder);
    }
}
