package com.fixme.market;

import java.io.*;
import java.net.*;

public class MarketMain {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5001);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Read assigned ID
        String assigned = in.readLine();
        System.out.println("Market connected. ID: " + assigned);

        // Listen for broker messages
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Market received: " + line);

            // Very simple: accept all orders
            out.println("Executed: " + line);
        }
    }
}

