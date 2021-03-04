package com.company.server;

import java.util.Calendar;

public class Server {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("example of usage: <java> <path.Server> <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        System.out.println("[SERVER] has started [port:" + port +
                "] [time: " + Calendar.getInstance().getTime() + "]");
        new ServerGame(port).start();
    }
}
