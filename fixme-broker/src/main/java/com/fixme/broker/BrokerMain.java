package com.fixme.broker;

import com.fixme.common.FixMessage;

import java.io.*;
import java.net.*;

public class BrokerMain {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Read assigned ID
        String assigned = in.readLine();
        int id = Integer.parseInt(assigned.split(":")[1]);
        System.out.println("Broker connected. ID: " + id);

        // Example: send a BUY order to market with ID 200000
        // FixMessage msg = new FixMessage()
        //     .set("ID", id)
        //     .set("Target", "200000")
        //     .set("Type", "BUY")
        //     .set("Instrument", "IBM")
        //     .set("Qty", 10)
        //     .set("Price", 135.50);
        // out.println(msg.toString());

        FixMessage msg = new FixMessage()
            .set("MsgType", "D")         // New Order - Single
            .set("SenderCompID", id)
            .set("TargetCompID", "200000")
            .set("Symbol", "IBM")
            .set("Side", 1)            // 1 = Buy, 2 = Sell
            .set("OrderQty", 10)
            .set("Price", 135.50);

        out.println(msg.toString());

        // Listen for responses
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Broker received: " + line);
        }

        socket.close();
    }
}

