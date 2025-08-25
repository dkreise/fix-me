package com.fixme.broker;

import com.fixme.common.FixMessage;

import java.io.*;
import java.net.*;
import java.util.Random;

public class BrokerMain {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Read assigned ID
        String assigned = in.readLine();
        int id = Integer.parseInt(assigned.split(":")[1]);
        System.out.println("Broker connected. ID: " + id);

        // instruments
        String[] symbols = {"AAPL", "GOOG", "MSFT", "AMZN", "TSLA", "FB", "NFLX", "NVDA", "ADBE", "INTC"};
        Random random = new Random();

        placeRandomOrder(id, out, symbols, random);
        
        // FixMessage msg = new FixMessage()
        //     .set("MsgType", "D")         // New Order - Single
        //     .set("SenderCompID", id)
        //     .set("TargetCompID", "200000")
        //     .set("Symbol", "IBM")
        //     .set("Side", 1)            // 1 = Buy, 2 = Sell
        //     .set("OrderQty", 10)
        //     .set("Price", 135.50);

        // out.println(msg.toString());

        // Listen for responses
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Broker received: " + line);
            // Sleep a bit and place another random order
            Thread.sleep(2000 + random.nextInt(3000));
            placeRandomOrder(id, out, symbols, random);
        }

        socket.close();
    }

    private static void placeRandomOrder(int brokerId, PrintWriter out, String[] symbols, Random random) {
        String symbol = symbols[random.nextInt(symbols.length)];
        int side = random.nextBoolean() ? 1 : 2; // 1=Buy, 2=Sell
        int qty = 1 + random.nextInt(100);
        double price = 100 + random.nextDouble() * 50; // price between 100 and 150

        FixMessage msg = new FixMessage()
            .set("MsgType", "D")         // New Order - Single
            .set("SenderCompID", brokerId)
            .set("TargetCompID", "200000")
            .set("Symbol", symbol)
            .set("Side", side)           // 1 = Buy, 2 = Sell
            .set("OrderQty", qty)
            .set("Price", price);

        out.println(msg.toString());
    }
}

