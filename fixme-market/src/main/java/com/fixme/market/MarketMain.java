package com.fixme.market;

import com.fixme.common.FixMessage;
import com.fixme.common.FixValidator;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MarketMain {
    private static final Random random = new Random();
    private static final Map<String, Double> instruments = new HashMap<>();
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5001);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Read assigned ID
        String assigned = in.readLine();
        int id = Integer.parseInt(assigned.split(":")[1]);
        System.out.println("Market connected. ID: " + id);

        // Initialize with some random instruments
        initRandomInstruments();
        System.out.println("Market " + id + " trades: " + instruments);

        // Listen for broker messages
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println("Market " + id + " received: " + line);

            FixMessage msg = new FixMessage(line);
            if (!FixValidator.isValid(msg)) {
                System.out.println("Market " + id + " rejected invalid message: " + line);
                continue;
            }

            String msgType = msg.get("MsgType");
            if ("D".equals(msgType)) { // Only process New Order - Single
                System.out.println("Market " + id + " processing order: " + msg);
                handleNewOrder(msg, id, out);
            } else {
                System.out.println("Market " + id + " ignoring unsupported MsgType: " + msgType);
                continue;
            }
        }

        socket.close();
    }

    private static void initRandomInstruments() {
        String[] symbols = {"AAPL", "GOOG", "MSFT", "AMZN", "TSLA", "FB", "NFLX", "NVDA", "ADBE", "INTC"};
        int count = 5 + random.nextInt(5);
        for (int i = 0; i < count; i++) {
            String symbol = symbols[random.nextInt(symbols.length)];
            if (!instruments.containsKey(symbol)) {
                double price = 50 + random.nextDouble() * 200; // price between 50 and 250
                instruments.put(symbol, price);
            }
        }
    }

    private static void handleNewOrder(FixMessage msg, int marketId, PrintWriter out) {
        String symbol = msg.get("Symbol");
        int side = msg.getInt("Side"); // 1=Buy, 2=Sell
        int qty = msg.getInt("OrderQty");
        double price = msg.getDouble("Price");

        FixMessage response = new FixMessage();
        response.set("BeginString", "FIX.4.4");
        response.set("MsgType", "8"); // Execution Report
        response.set("SenderCompID", marketId);
        response.set("TargetCompID", msg.getInt("SenderCompID"));
        response.set("Symbol", symbol);
        response.set("OrderQty", qty);
        response.set("Price", price);

        Double marketPrice = instruments.get(symbol);
        if (marketPrice == null) {
            response.set("ExecType", "8"); // Rejected
            response.set("OrdStatus", "8"); // Rejected
            response.set("Text", "Unknown instrument: " + symbol);
            System.out.println("Market " + marketId + " rejecting order for unknown instrument: " + symbol);
        } else if ((side == 1 && price >= marketPrice) || (side == 2 && price <= marketPrice)) {
            response.set("ExecType", "0"); // New
            response.set("OrdStatus", "2"); // Filled
            // response.set("LastQty", qty);
            // response.set("LastPx", marketPrice);
            System.out.println("Market " + marketId + " accepted order: " + msg);
        } else {
            response.set("ExecType", "8"); // Rejected
            response.set("OrdStatus", "8"); // Rejected
            response.set("Text", "Price mismatch, market price: " + marketPrice);
            System.out.println("Market " + marketId + " rejecting order due to price mismatch: " + msg);
        }

        out.println(response.toString());
    }
}

