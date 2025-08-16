package com.fixme.broker;

import java.io.*;
import java.net.*;

public class BrokerMain {
    public static void main(String[] args) throws IOException {
        System.out.println("✅ Broker started");

        Socket socket = new Socket("localhost", 5000);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("BUY Apple 10");

        System.out.println("Broker sent order: BUY Apple 10");
    }
}
