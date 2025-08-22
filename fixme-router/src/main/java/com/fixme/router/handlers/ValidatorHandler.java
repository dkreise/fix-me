package com.fixme.router.handlers;

import com.fixme.common.FixMessage;

public class ValidatorHandler extends Handler {
    @Override
    public void handle(FixMessage message) {
        // Validate the message
        if (isValid(message)) {
            System.out.println("Message is VALID: " + message);
            super.handle(message); // Call next handler in the chain
        } else {
            System.out.println("INVALID message: " + message);
            // Handle invalid message (e.g., log, send error response, etc.)
        }
    }

    private boolean isValid(FixMessage message) {
        // Implement validation logic here
        // For example, check required fields, format, etc.
        return true; // Placeholder for actual validation logic
    }
    
}
