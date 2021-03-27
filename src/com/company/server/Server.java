package com.company.server;

import java.util.*;

public class Server {

    /**  Server strategy:<p><p>
     *
     *  CONNECTION:
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
