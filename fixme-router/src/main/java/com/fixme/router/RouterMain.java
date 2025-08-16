package com.fixme.router;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RouterMain {
    private static final int BROKER_PORT = 5000;
    private static final int MARKET_PORT = 5001;
    private static final AtomicInteger idCounter = new AtomicInteger(100000);
    private static final ConcurrentHashMap<Integer, Socket> brokers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Socket> markets = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println("✅ Router started on ports 5000 (Brokers) and 5001 (Markets)");

        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(() -> listenForClients(BROKER_PORT, brokers, "broker"));
        executor.submit(() -> listenForClients(MARKET_PORT, markets, "markets"));
    }

    private static void listenForClients(int port, ConcurrentHashMap<Integer, Socket> table, String type) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening for " + type + "s on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                int id = idCounter.getAndIncrement();
                table.put(id, socket);
                System.out.println("Connected " + type + " with ID " + id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
