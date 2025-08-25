package com.fixme.common;

public class FixValidator {
    public static boolean isValid(FixMessage msg) {
        // check BeginString
        if (!"FIX.4.4".equals(msg.get("BeginString"))) {
            System.out.println("Invalid BeginString: " + msg.get("BeginString"));
            return false;
        }

        // check BodyLength
        int realLen = msg.calculateBodyLength();
        if (realLen != msg.getInt("BodyLength")) {
            System.out.println("Invalid BodyLength: " + msg.getInt("BodyLength") + ", expected: " + realLen);
            return false;
        }

        // check CheckSum
        int expected = computeCheckSum(msg);
        if (expected != msg.getInt("CheckSum")) {
            System.out.println("Invalid CheckSum: " + msg.getInt("CheckSum") + ", expected: " + expected);
            return false;
        }

        // check required tags depending on MsgType
        String msgType = msg.get("MsgType");
        if ("D".equals(msgType)) { // NewOrderSingle
            if (msg.get("Symbol") == null ||
                msg.get("Side") == null ||
                msg.get("OrderQty") == null) {
                System.out.println("Missing required field(s) for NewOrderSingle (35=D)");
                return false;
            }
        }

        return true;
    }

    public static int computeCheckSum(FixMessage msg) {
        int sum = 0;
        for (char c : msg.buildWithoutCheckSum().toCharArray()) {
            sum += c;
        }

        return sum % 256;
    }
}
