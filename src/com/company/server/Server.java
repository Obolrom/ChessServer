package com.company.server;

import java.util.*;

public class Server {
    public static final String directoryPath =
            System.getProperty("user.dir") + "/src/";
    public static final String savedGamesPath = "com/savedgames";
    public static final String usageGuide = "example of usage: <port> [pool size]";

    /**  <strong>Server strategy:<p></strong>
     *
     *  <strong>CONNECTION</strong>:
     *    RequestHandler accepts all the clients
     *    and push it to the collection, that is
     *    stored in ClientDispatcher
     */

    public static void main(String[] args) {
        if (args.length < 1 | args.length > 2) {
            System.err.println(usageGuide);
            System.exit(1);
        }

        int port = 0;
        int poolSize = 4;
        try {
            port = Integer.parseInt(args[0]);
            poolSize = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("invalid argument format");
            System.exit(2);
        } catch (ArrayIndexOutOfBoundsException ignored) { }
        System.out.println("[SERVER] has started [port:" + port +
                "] [time: " + Calendar.getInstance().getTime() + "]");

        new ClientListener(port, poolSize).run();
    }
}
