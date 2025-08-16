package com.fixme.market;

import java.io.*;
import java.net.*;

public class MarketMain {
    public static void main(String[] args) throws IOException {
        System.out.println("✅ Market started");

        Socket socket = new Socket("localhost", 5001);

        System.out.println("Market connected to Router.");
    }
}
