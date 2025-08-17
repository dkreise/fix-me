package com.fixme.router;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RouterMain {
    private static final int BROKER_PORT = 5000;
    private static final int MARKET_PORT = 5001;
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final AtomicInteger brokerIdCounter = new AtomicInteger(100000);
    private static final AtomicInteger marketIdCounter = new AtomicInteger(200000);
    private static final ConcurrentHashMap<Integer, Socket> brokers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Socket> markets = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println("✅ Router started on ports 5000 (Brokers) and 5001 (Markets)");

        executor.submit(() -> listenForClients(BROKER_PORT, "BROKER"));
        executor.submit(() -> listenForClients(MARKET_PORT, "MARKET"));
    }

    // private static void listenForClients(int port, ConcurrentHashMap<Integer, Socket> table, String type) {
    //     try (ServerSocket serverSocket = new ServerSocket(port)) {
    //         System.out.println("Listening for " + type + "s on port " + port);

    //         while (true) {
    //             Socket socket = serverSocket.accept();
    //             int id = idCounter.getAndIncrement();
    //             table.put(id, socket);
    //             System.out.println("Connected " + type + " with ID " + id);
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    private static void listenForClients(int port, String type) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening for " + type + "s on port " + port);
            while (true) {
                Socket client = serverSocket.accept();
                int id = (type.equals("BROKER")) ? brokerIdCounter.getAndIncrement() : marketIdCounter.getAndIncrement();
                if (type.equals("BROKER")) 
                    brokers.put(id, client);
                else 
                    markets.put(id, client);

                System.out.println(type + " connected with ID: " + id);

                // Send back assigned ID
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("ASSIGNED_ID:" + id);

                // Start handler task
                executor.submit(() -> handleClient(client, id, type));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket client, int id, String type) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(type + " " + id + " says: " + line);

                // Parse message format: "TARGET:msg"
                String[] parts = line.split(":", 2);
                if (parts.length < 2) continue;
                int targetId = Integer.parseInt(parts[0]);
                String msg = parts[1];

                // Forward to target (if exists)
                Socket target = (type.equals("BROKER")) ? markets.get(targetId) : brokers.get(targetId);
                if (target != null) {
                    PrintWriter out = new PrintWriter(target.getOutputStream(), true);
                    out.println(msg);
                } else {
                    System.out.println("Target " + targetId + " not found!");
                }
            }
        } catch (IOException e) {
            System.out.println(type + " " + id + " disconnected.");
        }
    }
}
