package com.fixme.broker;

import java.io.*;
import java.net.*;

public class BrokerMain {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Read assigned ID
        String assigned = in.readLine();
        System.out.println("Broker connected. ID: " + assigned);

        // Example: send a BUY order to market with ID 200000
        out.println("200000:BUY Instrument=IBM Qty=10 Price=135.50");

        // Listen for responses
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Broker received: " + line);
        }
    }
}

