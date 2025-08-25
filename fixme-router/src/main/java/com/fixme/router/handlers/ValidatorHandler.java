package com.fixme.router.handlers;

import com.fixme.common.FixMessage;
import com.fixme.common.FixValidator;

public class ValidatorHandler extends Handler {
    @Override
    public void handle(FixMessage message) {
        // Validate the message
        if (FixValidator.isValid(message)) {
            System.out.println("Message is VALID: " + message);
            super.handle(message); // Call next handler in the chain
        } else {
            System.out.println("INVALID message: " + message);
            // Handle invalid message (e.g., log, send error response, etc.)
        }
    }    
}
