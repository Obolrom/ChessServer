package com.company.server;

import com.example.customchess.engine.Game;
import com.example.customchess.engine.OneDeviceGame;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {

    /**  <strong>Server strategy:<p></strong>
     *
     *  <strong>CONNECTION</strong>:
     *    RequestHandler accepts all the clients
     *    and push it to the collection, that is
     *    stored in ClientDispatcher <p>
     *    <p> 1) before pushing, check for
     *           existing of pair with the same GAME_ID
     *    <p> 2) check for existing of current game on server
     *           with same ID or serialized IDs
     */

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("example of usage: <java> <path.Server> <port>");
            System.exit(1);
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("invalid port format");
            System.exit(2);
        }
        System.out.println("[SERVER] has started [port:" + port +
                "] [time: " + Calendar.getInstance().getTime() + "]");

        new RequestHandler(port, 3).run();
    }
}
