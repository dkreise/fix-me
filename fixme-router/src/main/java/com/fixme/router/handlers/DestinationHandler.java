package com.fixme.router.handlers;

import com.fixme.common.FixMessage;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class DestinationHandler extends Handler {
    private final ConcurrentHashMap<Integer, Socket> brokers;
    private final ConcurrentHashMap<Integer, Socket> markets;

    public DestinationHandler(ConcurrentHashMap<Integer, Socket> brokers, 
                              ConcurrentHashMap<Integer, Socket> markets) {
        this.brokers = brokers;
        this.markets = markets;
    }

    @Override
    public void handle(FixMessage message) {
        int targetId = message.getInt("TargetCompID");
        Socket destination = null;
        if (brokers.containsKey(targetId)) {
            destination = brokers.get(targetId);
        } else if (markets.containsKey(targetId)) {
            destination = markets.get(targetId);
        } else {
            System.out.println("No destination found for Target ID: " + targetId);
            // Handle case where no destination is found (e.g., log, send error response, etc.)
            return;
        }

        message.setDestination(destination);
        System.out.println("Message destination set to: " + destination);
        super.handle(message); 
    }
    
}
