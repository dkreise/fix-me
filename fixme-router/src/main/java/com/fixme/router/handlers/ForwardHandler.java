package com.fixme.router.handlers;

import com.fixme.common.FixMessage;
import java.io.PrintWriter;
import java.net.Socket;

public class ForwardHandler extends Handler {
    
    @Override
    public void handle(FixMessage message) {
        Socket destination = message.getDestination();
        if (destination != null) {
            try {
                PrintWriter out = new PrintWriter(destination.getOutputStream(), true);
                out.println(message.toString());
            } catch (Exception e) {
                System.out.println("Failed to forward message to destination: " + e.getMessage());
                // e.printStackTrace();
            }
        }
        
        super.handle(message);
    }

    // Additional methods for forwarding logic can be added here
    
}
