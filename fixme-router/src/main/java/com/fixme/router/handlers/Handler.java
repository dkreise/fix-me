package com.fixme.router.handlers;

import com.fixme.common.FixMessage;

public abstract class Handler {
    protected Handler next;

    public Handler linkWith(Handler next) {
        System.out.println("LINKING HANDLER " + this.getClass().getSimpleName() + " -> " + next.getClass().getSimpleName());
        this.next = next;
        return this.next;
    }

    public void handle(FixMessage message) {
        System.out.println("HANDLER " + this.getClass().getSimpleName() + " HANDLING MESSAGE: " + message);
        if (next != null) {
            System.out.println("CALLING NEXT HANDLER");
            next.handle(message);
        } else {
            System.out.println("CHAIN STOPPED");
        }
    }
}
