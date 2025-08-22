package com.fixme.market;

import com.fixme.common.FixMessage;

import java.io.*;
import java.net.*;

public class MarketMain {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5001);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Read assigned ID
        String assigned = in.readLine();
        int id = Integer.parseInt(assigned.split(":")[1]);
        System.out.println("Market connected. ID: " + id);

        // Listen for broker messages
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Market received: " + line);

            FixMessage msg = new FixMessage(line);
            int brokerId = msg.getInt("SenderCompID");
            msg.set("TargetCompID", brokerId);
            msg.set("SenderCompID", id);
            // Very simple: accept all orders
            out.println(msg);
        }

        socket.close();
    }
}

